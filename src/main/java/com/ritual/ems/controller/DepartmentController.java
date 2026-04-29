package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.model.Department;
import com.ritual.ems.service.DepartmentService;
import com.ritual.ems.dto.request.DepartmentRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public Result<List<Department>> getAllDepartments() {
        return Result.success(departmentService.getAllDepartments());
    }

    @PostMapping
    public Result<String> createDepartment(@RequestBody DepartmentRequest request) {
        departmentService.createDepartment(request);
        return Result.success("Department created successfully");
    }

    @PutMapping("/{deptId}")
    public Result<String> updateDepartment(@PathVariable("deptId") Integer deptId,
                                           @RequestBody DepartmentRequest request) {
        departmentService.updateDepartment(deptId, request);
        return Result.success("Department updated successfully");
    }

    @DeleteMapping("/{deptId}")
    public Result<String> deleteDepartment(@PathVariable("deptId") Integer deptId) {
        departmentService.deleteDepartment(deptId);
        return Result.success("Department deleted successfully");
    }
}
