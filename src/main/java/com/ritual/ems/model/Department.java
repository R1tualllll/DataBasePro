package com.ritual.ems.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private Integer deptId;
    private String deptName;
    private String deptCode;
    private String managerName;
    private String phone;
    private String location;
}
