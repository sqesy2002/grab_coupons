--- 抢券原子脚本
--- KEYS[1]: 库存key (e.g., coupon:stock:{couponId})
--- KEYS[2]: 用户已购数量hash key (e.g., coupon:user:count:{couponId})
--- KEYS[3]: 活动开关 gate key (e.g., coupon:gate:{couponId})
--- ARGV[1]: 用户ID (e.g., userId)
--- ARGV[2]: 每人限购数量 (e.g., 2)

-- 1. 检查活动是否开始
local gate = redis.call('GET', KEYS[3])
if (gate == false or gate == '0') then
    return 3 -- 3: 活动尚未开始
end

-- 2. 查询用户已购数量
local userCount = redis.call('HGET', KEYS[2], ARGV[1])
if (userCount and tonumber(userCount) >= tonumber(ARGV[2])) then
    return 1 -- 1: 已达到抢购上限
end

-- 3. 检查库存
local stock = redis.call('LLEN', KEYS[1])
if (stock <= 0) then
    return 2 -- 2: 库存不足
end

-- 4. 抢券 (弹出库存)
redis.call('RPOP', KEYS[1])

-- 5. 增加用户已购数量
redis.call('HINCRBY', KEYS[2], ARGV[1], 1)

return 0 -- 0: 成功