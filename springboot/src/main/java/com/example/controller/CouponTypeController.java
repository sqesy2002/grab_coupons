package com.example.controller;

import com.example.common.Result;
import com.example.entity.CouponType;
import com.example.service.CouponTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponTypeController {

    @Resource
    private CouponTypeService couponTypeService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<CouponType> list = couponTypeService.selectAll();
        return Result.success(list);
    }
}