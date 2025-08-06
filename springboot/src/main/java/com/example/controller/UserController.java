package com.example.controller;

import com.example.common.Result;
import com.example.entity.User;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<User> list = userService.selectAll();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<User> userPageInfo = userService.selectPage(pageNum, pageSize);
        return Result.success(userPageInfo);
    }

    /**
     * 新增：自动生成指定数量的用户的接口
     * @param quantity 要生成的用户数量
     * @return 操作结果
     */
    @GetMapping("/generate")
    public Result generateUsers(@RequestParam("user_quantity") int quantity) {
        if (quantity > 20000) {
            // 出于安全考虑，可以限制一次生成的最大数量
            return Result.error();
        }
        userService.generateAndSaveUsers(quantity);
        return Result.success_msg("成功生成 " + quantity + " 个用户。");

    }

    /**
     * 新增：删除所有用户数据的接口
     * @return 操作结果
     */
    @GetMapping ("/deleteAll")
    public Result deleteAll() {
        userService.deleteAllUsers();
        return Result.success_msg("已成功删除所有用户数据。");
    }
}