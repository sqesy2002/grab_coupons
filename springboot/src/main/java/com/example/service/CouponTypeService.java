package com.example.service;

import com.example.entity.CouponType;
import com.example.entity.User;
import com.example.mapper.CouponTypeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponTypeService {

    @Resource
    private CouponTypeMapper couponTypeMapper;

    public List<CouponType> selectAll() {
        return couponTypeMapper.selectAll();
    }

    /**
     * 新增：分页查询券种类的方法
     * @param pageNum  当前页码
     * @param pageSize 每页显示的条数
     * @return 包含分页信息的用户数据 (PageInfo)
     */
    public PageInfo<CouponType> selectPage(Integer pageNum, Integer pageSize) {
        // 在调用 mapper 查询方法之前，只需调用 PageHelper.startPage()
        // 它会“自动”对紧随其后的第一个 Mybatis 查询进行物理分页
        PageHelper.startPage(pageNum, pageSize);
        List<CouponType> couponList = couponTypeMapper.selectAll();

        return new PageInfo<>(couponList); // 将查询出的列表传入 PageInfo 对象，它会计算出总页数、总记录数等信息
    }

    /**
     * 新增一个优惠券种类
     * @param couponType 从前端接收到的优惠券数据
     * @return 返回插入数据库后的完整 CouponType 对象（包含ID）
     */
    @Transactional
    public CouponType addCouponType(CouponType couponType) {
        // 设置服务器端的默认值
        LocalDateTime now = LocalDateTime.now();
        couponType.setInsertTime(now);
        couponType.setUpdateTime(now);
        // 通常 inserter 和 updater 应该从当前登录用户获取，这里我们先硬编码为 1
        couponType.setInserter(1);
        couponType.setUpdater(1);

        couponTypeMapper.insert(couponType);

        return couponType;
    }
}