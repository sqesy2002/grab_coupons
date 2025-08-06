package com.example.controller;

import com.example.common.Result;
import com.example.entity.GrabCouponRecord;
import com.example.service.GrabCouponRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/record")
public class GrabCouponRecordController {

    @Resource
    private GrabCouponRecordService grabCouponRecordService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<GrabCouponRecord> list = grabCouponRecordService.selectAll();
        return Result.success(list);
    }
}