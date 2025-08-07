package com.example.mapper;

import com.example.entity.GrabCouponRecord;

import java.util.List;

public interface GrabCouponRecordMapper {
    List<GrabCouponRecord> selectAll();

    /**
     * 插入：存一条记录
     */
    void insert(GrabCouponRecord record);
}
