package com.example.service;

import com.example.common.Result;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 查询：查出表中所有用户的方法
     */
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    /**
     * 新增：分页查询用户的方法
     * @param pageNum  当前页码
     * @param pageSize 每页显示的条数
     * @return 包含分页信息的用户数据 (PageInfo)
     */
    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize) {
        // 在调用 mapper 查询方法之前，只需调用 PageHelper.startPage()
        // 它会“自动”对紧随其后的第一个 Mybatis 查询进行物理分页
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAll();

        return new PageInfo<>(userList); // 将查询出的列表传入 PageInfo 对象，它会计算出总页数、总记录数等信息
    }

    /**
     * 新增：生成并批量保存用户
     * @param userQuantity 要生成的用户数量
     */
    @Transactional // 加上事务注解，保证数据一致性 (能确保这批用户要么全部插入成功，要么在出现任何错误时全部回滚)
    public void generateAndSaveUsers(Integer userQuantity) {
        if (userQuantity <= 0) {
            return; // 如果数量不合法，直接返回
        }

        List<User> userList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(); // 获取当前时间

        for (int i = 1; i <= userQuantity; i++) {
            User user = new User();

            // 根据你的要求生成数据
            user.setNickname("vovo" + i); // e.g., vovo1, vovo2
            user.setName("Mike" + i); // e.g., Mike1, Mike2
            user.setPhoneNumber(String.valueOf(131100 + i -1)); // e.g., 131100, 131101
            user.setStatus(User.Status.active); // 统一为 active
            user.setPasswordHash("123"); // 统一为 '123'
            user.setInserter(1); // 统一为 1
            user.setUpdater(1); // 统一为 1
            user.setInsertTime(now);
            user.setUpdateTime(now);

            userList.add(user);
        }

        // 调用 Mapper 的批量插入方法
        userMapper.insertBatch(userList);
    }

    /**
     * 新增：删除所有用户数据
     */
    @Transactional
    public void deleteAllUsers() {
        userMapper.deleteAll();
    }
}