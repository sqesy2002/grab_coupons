package com.example.controller;

import com.example.common.Result;
import com.example.entity.GrabCouponRecord;
import com.example.service.GrabCouponRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grab")
public class GrabCouponRecordController {

    @Resource
    private GrabCouponRecordService grabCouponRecordService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<GrabCouponRecord> list = grabCouponRecordService.selectAll();
        return Result.success(list);
    }

    @PostMapping("/async")
    public Result simulateAsyncGrab(
            @RequestParam Integer couponId,
            @RequestParam Integer couponQuantity,
            @RequestParam Long startTime // 接收北京时间的秒级时间戳
    ) {
        try {
            double duration = grabCouponRecordService.simulateGrab(couponId, couponQuantity, startTime);
            System.out.println("模拟抢券总耗时: " + duration + " 秒");
            return Result.success(duration);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }
}