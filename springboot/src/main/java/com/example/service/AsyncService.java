package com.example.service;

import com.example.entity.GrabCouponRecord;
import com.example.entity.UserCouponPackage;
import com.example.mapper.GrabCouponRecordMapper;
import com.example.mapper.UserCouponPackageMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

@Service
public class AsyncService {

    @Resource
    private GrabCouponRecordMapper grabCouponRecordMapper;

    @Resource
    private UserCouponPackageMapper userCouponPackageMapper;

    @Async // 声明为异步方法
    @Transactional // 声明为事务方法，保证两个写操作要么都成功，要么都失败
    public void handleSuccessfulGrab(Integer userId, Integer couponId, CountDownLatch latch) {
        try {
            // 插入抢券成功记录
            GrabCouponRecord grabRecord = new GrabCouponRecord();
            grabRecord.setUserId(userId);
            grabRecord.setCouponId(couponId);
            grabRecord.setInsertTime(LocalDateTime.now());
            grabCouponRecordMapper.insert(grabRecord);

            // 更新或插入用户券包
            UserCouponPackage ucp = new UserCouponPackage();
            ucp.setUserId(userId);
            ucp.setCouponId(couponId);
            LocalDateTime now = LocalDateTime.now();
            ucp.setInsertTime(now); // insert时会使用
            ucp.setUpdateTime(now); // insert和update时都会使用
            userCouponPackageMapper.upsert(ucp);

        } finally {
            // 关键：无论成功还是失败，都必须调用countDown，否则主线程会一直等待
            latch.countDown();
        }
    }
}