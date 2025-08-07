package com.example.mapper;

import com.example.entity.UserCouponPackage;

import java.util.List;

public interface UserCouponPackageMapper {
    /**
     * 查询：查出所有种类的券
     */
    List<UserCouponPackage> selectAll();
}
