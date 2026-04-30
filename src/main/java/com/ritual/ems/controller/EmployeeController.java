package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.dto.request.EmployeeRequest;
import com.ritual.ems.dto.request.ResignRequest;
import com.ritual.ems.dto.response.EmployeeDetailResponse;
import com.ritual.ems.dto.response.PageResponse;
import com.ritual.ems.model.Employee;
import com.ritual.ems.service.EmployeeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/search")
    public Result<PageResponse<Employee>> searchEmployees(@RequestParam(value = "keyword", required = false) String keyword,
                                                          @RequestParam(value = "deptId", required = false) Integer deptId,
                                                          @RequestParam(value = "positionId", required = false) Integer positionId,
                                                          @RequestParam(value = "status", required = false) String status,
                                                          @RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return Result.success(employeeService.searchEmployees(keyword, deptId, positionId, status, page, pageSize));
    }

    @GetMapping("/{empId}/detail")
    public Result<EmployeeDetailResponse> getEmployeeDetail(@PathVariable("empId") Integer empId) {
        return Result.success(employeeService.getEmployeeDetail(empId));
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

    @PutMapping("/{empId}/regularize")
    public Result<String> regularizeEmployee(@PathVariable("empId") Integer empId) {
        employeeService.regularizeEmployee(empId);
        return Result.success("Employee regularized successfully");
    }

    @PutMapping("/{empId}/resign")
    public Result<String> resignEmployee(@PathVariable("empId") Integer empId,
                                         @RequestBody ResignRequest request) {
        employeeService.resignEmployee(empId, request);
        return Result.success("Employee resigned successfully");
    }

    @DeleteMapping("/{empId}")
    public Result<String> deleteEmployee(@PathVariable("empId") Integer empId) {
        employeeService.deleteEmployee(empId);
        return Result.success("Employee deleted successfully");
    }
}
