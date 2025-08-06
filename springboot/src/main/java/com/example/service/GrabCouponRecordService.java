package com.example.service;

import com.example.entity.GrabCouponRecord;
import com.example.mapper.GrabCouponRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrabCouponRecordService {

    @Resource
    private GrabCouponRecordMapper grabCouponRecordMapper;

    public List<GrabCouponRecord> selectAll() {
        return grabCouponRecordMapper.selectAll();
    }
}
