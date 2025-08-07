package com.example.mapper;

import com.example.entity.UserCouponPackage;

import java.util.List;

public interface UserCouponPackageMapper {
    /**
     * 查询：查出所有种类的券
     */
    List<UserCouponPackage> selectAll();

    /**
     * 更新或插入：没该uid与cid对应的券包就插入，有就更新，使券的数量+1
     */
    void upsert(UserCouponPackage record);
}
