package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.dto.request.EmployeeRequest;
import com.ritual.ems.model.Employee;
import com.ritual.ems.service.EmployeeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/employee", "/api/employees"})
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public Result<List<Employee>> getAllEmployees() {
        return Result.success(employeeService.getAllEmployees());
    }

    @PostMapping
    public Result<String> createEmployee(@RequestBody EmployeeRequest request) {
        employeeService.createEmployee(request);
        return Result.success("Employee created successfully");
    }

    @PutMapping("/{empId}")
    public Result<String> updateEmployee(@PathVariable("empId") Integer empId,
                                         @RequestBody EmployeeRequest request) {
        employeeService.updateEmployee(empId, request);
        return Result.success("Employee updated successfully");
    }

    @DeleteMapping("/{empId}")
    public Result<String> deleteEmployee(@PathVariable("empId") Integer empId) {
        employeeService.deleteEmployee(empId);
        return Result.success("Employee deleted successfully");
    }
}
