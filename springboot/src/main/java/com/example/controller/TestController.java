package com.example.controller;

import com.example.common.Result;
import com.example.entity.Test;
import com.example.service.TestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Test> list = testService.selectAll();
        return Result.success(list);
    }
}
