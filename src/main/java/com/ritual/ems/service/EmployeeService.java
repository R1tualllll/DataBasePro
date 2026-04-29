package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.EmployeeRequest;
import com.ritual.ems.model.Employee;
import com.ritual.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    private static final Set<String> VALID_GENDERS = Set.of("male", "female");
    private static final Set<String> VALID_STATUSES = Set.of("active", "resigned", "probation");

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void createEmployee(EmployeeRequest request) {
        validateEmployeeRequest(request);
        employeeRepository.save(request);
    }

    public void updateEmployee(Integer empId, EmployeeRequest request) {
        validateEmployeeId(empId);
        validateEmployeeRequest(request);

        int rows = employeeRepository.update(empId, request);
        if (rows == 0) {
            throw new BusinessException("员工不存在");
        }
    }

    public void deleteEmployee(Integer empId) {
        validateEmployeeId(empId);

        int rows = employeeRepository.delete(empId);
        if (rows == 0) {
            throw new BusinessException("员工不存在");
        }
    }

    private void validateEmployeeRequest(EmployeeRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getEmpName() == null || request.getEmpName().isBlank()) {
            throw new BusinessException("员工姓名不能为空");
        }
        if (request.getGender() == null || request.getGender().isBlank()) {
            throw new BusinessException("员工性别不能为空");
        }
        if (!VALID_GENDERS.contains(request.getGender())) {
            throw new BusinessException("员工性别只能是 male 或 female");
        }
        if (request.getAge() == null) {
            throw new BusinessException("员工年龄不能为空");
        }
        if (request.getAge() < 18 || request.getAge() > 65) {
            throw new BusinessException("员工年龄必须在 18 到 65 之间");
        }
        if (request.getHireDate() == null) {
            throw new BusinessException("入职日期不能为空");
        }
        if (request.getDeptId() == null || request.getDeptId() <= 0) {
            throw new BusinessException("部门ID不合法");
        }
        if (request.getPositionId() == null || request.getPositionId() <= 0) {
            throw new BusinessException("岗位ID不合法");
        }
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new BusinessException("员工状态不能为空");
        }
        if (!VALID_STATUSES.contains(request.getStatus())) {
            throw new BusinessException("员工状态只能是 active、resigned 或 probation");
        }
        if (request.getLeaveDate() != null && request.getLeaveDate().isBefore(request.getHireDate())) {
            throw new BusinessException("离职日期不能早于入职日期");
        }
    }

    private void validateEmployeeId(Integer empId) {
        if (empId == null || empId <= 0) {
            throw new BusinessException("员工ID不合法");
        }
    }
}
