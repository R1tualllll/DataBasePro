package com.ritual.ems.dto.response;

import com.ritual.ems.model.Department;
import com.ritual.ems.model.Employee;
import com.ritual.ems.model.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailResponse {
    private Employee employee;
    private Department department;
    private Position position;
}
