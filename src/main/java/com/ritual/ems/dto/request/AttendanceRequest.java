package com.ritual.ems.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private Integer empId;
    private LocalDate attendanceDate;
    private String attendanceStatus;
    private Integer lateMinutes;
    private BigDecimal overtimeHours;
    private String remark;
}
