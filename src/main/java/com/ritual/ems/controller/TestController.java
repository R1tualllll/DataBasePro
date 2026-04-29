package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/db")
    public Result<Map<String, Object>> testDatabaseConnection() {
        try {
            return Result.success(testService.testDatabaseConnection());
        } catch (Exception e) {
            return Result.failure(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
