package com.example.mapper;

import com.example.entity.User;

import java.util.List;

public interface UserMapper {
    /**
     * 查询：查出所有的用户
     */
    List<User> selectAll();

    /**
     * 新增：批量插入用户列表
     * @param userList 用户列表
     */
    void insertBatch(List<User> userList);

    /**
     * 新增：删除所有用户
     */
    void deleteAll();
}