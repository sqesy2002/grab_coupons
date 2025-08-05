package com.example.service;

import com.example.common.Result;
import com.example.entity.Test;
import com.example.mapper.TestMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Resource
    private TestMapper testMapper;

    public List<Test> selectAll() {
        return testMapper.selectAll();
    }
}
