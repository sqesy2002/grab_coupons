package com.example.service;

import com.example.entity.UserCouponPackage;
import com.example.mapper.UserCouponPackageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCouponPackageService {

    @Resource
    private UserCouponPackageMapper userCouponPackageMapper;

    public List<UserCouponPackage> selectAll() {
        return userCouponPackageMapper.selectAll();
    }
}