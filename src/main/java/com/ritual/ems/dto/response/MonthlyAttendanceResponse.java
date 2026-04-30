package com.ritual.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAttendanceResponse {
    private Integer empId;
    private String empName;
    private String deptName;
    private Long presentDays;
    private Long lateDays;
    private Long leaveDays;
    private Long absentDays;
    private Long totalLateMinutes;
    private BigDecimal totalOvertimeHours;
}
