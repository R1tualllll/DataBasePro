package com.ritual.ems.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
    private String empName;
    private String gender;
    private Integer age;
    private String phone;
    private String email;
    private LocalDate hireDate;
    private Integer deptId;
    private Integer positionId;
    private String status;
    private LocalDate leaveDate;
}
