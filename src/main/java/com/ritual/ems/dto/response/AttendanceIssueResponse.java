package com.ritual.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceIssueResponse {
    private Integer empId;
    private String empName;
    private LocalDate attendanceDate;
    private String attendanceStatus;
    private Integer lateMinutes;
}
