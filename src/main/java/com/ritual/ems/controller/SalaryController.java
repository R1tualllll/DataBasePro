package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.dto.request.SalaryRequest;
import com.ritual.ems.model.Salary;
import com.ritual.ems.service.SalaryService;
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
@RequestMapping({"/api/salary", "/api/salaries"})
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public Result<List<Salary>> getAllSalaries() {
        return Result.success(salaryService.getAllSalaries());
    }

    @PostMapping
    public Result<String> createSalary(@RequestBody SalaryRequest request) {
        salaryService.createSalary(request);
        return Result.success("Salary created successfully");
    }

    @PutMapping("/{salaryId}")
    public Result<String> updateSalary(@PathVariable("salaryId") Integer salaryId,
                                       @RequestBody SalaryRequest request) {
        salaryService.updateSalary(salaryId, request);
        return Result.success("Salary updated successfully");
    }

    @DeleteMapping("/{salaryId}")
    public Result<String> deleteSalary(@PathVariable("salaryId") Integer salaryId) {
        salaryService.deleteSalary(salaryId);
        return Result.success("Salary deleted successfully");
    }
}
