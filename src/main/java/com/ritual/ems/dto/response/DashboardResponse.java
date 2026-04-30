package com.ritual.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long activeEmployees;
    private Long probationEmployees;
    private Long monthlyAttendanceIssues;
    private BigDecimal monthlySalaryTotal;
    private List<StatisticsResponse> departmentRanking;
    private List<AttendanceIssueResponse> attendanceIssues;
}
