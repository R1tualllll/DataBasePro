package com.ritual.ems.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequest {
    private String deptName;
    private String deptCode;
    private String managerName;
    private String phone;
    private String location;
}
