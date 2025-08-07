package com.example.controller;

import com.example.common.Result;
import com.example.entity.CouponType;
import com.example.entity.User;
import com.example.service.CouponTypeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<CouponType> couponPageInfo = couponTypeService.selectPage(pageNum, pageSize);
        return Result.success(couponPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody CouponType couponType) {
        // 这里可以添加一些基础的参数校验，例如 couponName 不能为空等
        if (couponType.getCouponName() == null || couponType.getCouponName().isEmpty()) {
            return Result.error();
        }

        CouponType newCouponType = couponTypeService.addCouponType(couponType);
        return Result.success_msg(newCouponType.getCouponName() + "已成功创建！");
    }
}