package com.example.service;

import com.example.entity.CouponType;
import com.example.mapper.CouponTypeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponTypeService {

    @Resource
    private CouponTypeMapper couponTypeMapper;

    public List<CouponType> selectAll() {
        return couponTypeMapper.selectAll();
    }
}