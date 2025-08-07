package com.example.entity;

import java.time.LocalDateTime;

public class CouponType {
    private Integer couponId;
    private String couponName;
    private String description;  // 券描述
    private Integer maxPerUser;  // 一名用户最多可以持有多少张该种券
    private Integer validityDuration;  // 单位为天，该券的生效时间长度
    private LocalDateTime validTime;  // 该券的最早生效时间
    private LocalDateTime invalidTime;  // 该券在该时间后就无效下架，无法使用
    private Integer inserter;
    private LocalDateTime insertTime;
    private Integer updater;
    private LocalDateTime updateTime;

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxPerUser() {
        return maxPerUser;
    }

    public void setMaxPerUser(Integer maxPerUser) {
        this.maxPerUser = maxPerUser;
    }

    public Integer getValidityDuration() {
        return validityDuration;
    }

    public void setValidityDuration(Integer validityDuration) {
        this.validityDuration = validityDuration;
    }

    public LocalDateTime getValid_time() {
        return validTime;
    }

    public void setValid_time(LocalDateTime valid_time) {
        this.validTime = valid_time;
    }

    public LocalDateTime getInvalid_time() {
        return invalidTime;
    }

    public void setInvalid_time(LocalDateTime invalid_time) {
        this.invalidTime = invalid_time;
    }

    public Integer getInserter() {
        return inserter;
    }

    public void setInserter(Integer inserter) {
        this.inserter = inserter;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public Integer getUpdater() {
        return updater;
    }

    public void setUpdater(Integer updater) {
        this.updater = updater;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
