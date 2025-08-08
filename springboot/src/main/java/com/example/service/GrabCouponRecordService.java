package com.example.service;

import com.example.entity.CouponType;
import com.example.entity.GrabCouponRecord;
import com.example.mapper.CouponTypeMapper;
import com.example.mapper.GrabCouponRecordMapper;
import com.example.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
@Service
public class GrabCouponRecordService {

    @Resource
    private GrabCouponRecordMapper grabCouponRecordMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CouponTypeMapper couponTypeMapper;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Long> grabScript;  // 用于加载lua脚本的容器

    // 使用线程安全的固定大小线程池来模拟用户并发请求
    private static final ExecutorService USER_REQUEST_EXECUTOR = Executors.newFixedThreadPool(100);

    @PostConstruct
    public void init() {
        grabScript = new DefaultRedisScript<>();
        grabScript.setResultType(Long.class);
        grabScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/grab_coupon.lua")));
    }

    public double simulateGrab(Integer couponId, Integer couponQuantity, long startTimeSeconds) {
        // 0. 从数据库获取券的类型信息，主要是为了获取“每人限购数量”
        CouponType couponType = couponTypeMapper.selectById(couponId);
        if (couponType == null) {
            throw new RuntimeException("优惠券类型不存在: " + couponId);
        }
        Integer maxPerUser = couponType.getMaxPerUser();

        // 1. 准备Redis数据 (数据预热)
        prepareRedisData(couponId, couponQuantity);

        // 2. 获取所有用户ID
        List<Integer> userIds = userMapper.selectAllUserIds();
        System.out.println("参与抢券的用户总数: " + userIds.size());
        // 将用户列表的顺序随机打乱
        Collections.shuffle(userIds);
        System.out.println("用户ID已随机打乱，模拟无序请求。");

        // 3. 使用CountDownLatch来等待所有数据库操作完成
        CountDownLatch latch = new CountDownLatch(couponQuantity);

        // 4. 计算并等待抢券开始
        Instant startTime = Instant.ofEpochSecond(startTimeSeconds);
        long delayMillis = Duration.between(Instant.now(), startTime).toMillis();
        if (delayMillis > 0) {
            System.out.println("距离抢券开始还有 " + delayMillis / 1000.0 + " 秒...");
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 5. 开始抢券！打开活动阀门并记录开始时间
        stringRedisTemplate.opsForValue().set("coupon:gate:" + couponId, "1");
        Instant grabStartTime = Instant.now();
        System.out.println("抢券开始！时间: " + grabStartTime);


        // 6. 模拟所有用户同时发起请求
        for (Integer userId : userIds) {
            USER_REQUEST_EXECUTOR.submit(() -> {
                String stockKey = "coupon:stock:" + couponId;
                String userCountKey = "coupon:user:count:" + couponId;
                String gateKey = "coupon:gate:" + couponId;

                List<String> keys = List.of(stockKey, userCountKey, gateKey);

                // 使用循环来模拟用户重复抢券的行为
                while (true) {
                    // 调用Lua脚本
                    Long result = stringRedisTemplate.execute(
                            grabScript,
                            keys,
                            String.valueOf(userId),
                            String.valueOf(maxPerUser)
                    );

                    if (result == 0) {
                        // 抢券成功，调用异步服务处理数据库写入
                        asyncService.handleSuccessfulGrab(userId, couponId, latch);
                        // 成功后继续循环，尝试抢下一张
                    } else {
                        // 如果抢券失败（库存不足、达到个人上限等），则停止该用户的抢券行为
                        break;
                    }
                }
            });
        }

        // 7. 等待所有成功的抢券记录被写入数据库
        try {
            System.out.println("主线程等待所有数据库操作完成...");
            latch.await(10, TimeUnit.MINUTES); // 设置一个较长的超时时间防止死锁
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("等待数据库操作时被中断！");
        }

        Instant endTime = Instant.now();
        System.out.println("所有成功的抢券记录均已写入数据库！时间: " + endTime);

        // 8. 计算总耗时
        return Duration.between(grabStartTime, endTime).toMillis() / 1000.0;
    }

    /**
     * 本方法提前把库存与抢券的阀门加载进 Redis，避免抢券时才初始化导致延迟
     * @param couponId 券ID
     * @param couponQuantity 本次发布的券数量
     */
    private void prepareRedisData(Integer couponId, Integer couponQuantity) {
        String stockKey = "coupon:stock:" + couponId;
        String userCountKey = "coupon:user:count:" + couponId;
        String gateKey = "coupon:gate:" + couponId;

        // 清理旧数据
        stringRedisTemplate.delete(List.of(stockKey, userCountKey, gateKey));

        // 加载库存，将指定数量的优惠券库存加载到Redis的List中
        String[] coupons = IntStream.range(0, couponQuantity).mapToObj(i -> "1").toArray(String[]::new);
        if (coupons.length > 0) {
            stringRedisTemplate.opsForList().leftPushAll(stockKey, coupons);
        }

        // 设置活动阀门为关闭
        stringRedisTemplate.opsForValue().set(gateKey, "0");

        System.out.println("Redis数据预热完成。券ID: " + couponId + ", 数量: " + couponQuantity);
    }


    public List<GrabCouponRecord> selectAll() {
        return grabCouponRecordMapper.selectAll();
    }
}
