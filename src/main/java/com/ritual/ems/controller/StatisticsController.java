package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.dto.response.DashboardResponse;
import com.ritual.ems.dto.response.MonthlyAttendanceResponse;
import com.ritual.ems.dto.response.StatisticsResponse;
import com.ritual.ems.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/employees-by-department")
    public Result<List<StatisticsResponse>> countEmployeesByDepartment() {
        return Result.success(statisticsService.countEmployeesByDepartment());
    }

    @GetMapping("/employees-by-position")
    public Result<List<StatisticsResponse>> countEmployeesByPosition() {
        return Result.success(statisticsService.countEmployeesByPosition());
    }

    @GetMapping("/monthly-attendance")
    public Result<List<MonthlyAttendanceResponse>> monthlyAttendance(@RequestParam("month") String month) {
        return Result.success(statisticsService.monthlyAttendance(month));
    }

    @GetMapping("/dashboard")
    public Result<DashboardResponse> dashboard(@RequestParam("month") String month) {
        return Result.success(statisticsService.dashboard(month));
    }
}
