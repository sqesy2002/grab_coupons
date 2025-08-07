package com.example.mapper;

import com.example.entity.CouponType;

import java.util.List;

public interface CouponTypeMapper {
    List<CouponType> selectAll();

    /**
     * 插入一个新的优惠券种类
     * @param couponType 要插入的优惠券对象
     * @return 返回影响的行数
     */
    int insert(CouponType couponType);

    CouponType selectById(Integer couponId);
}
