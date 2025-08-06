package com.example.controller;

import com.example.common.Result;
import com.example.entity.UserCouponPackage;
import com.example.service.UserCouponPackageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/package")
public class UserCouponPackageController {

    @Resource
    private UserCouponPackageService userCouponPackageService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<UserCouponPackage> list = userCouponPackageService.selectAll();
        return Result.success(list);
    }
}