package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.DepartmentRequest;
import com.ritual.ems.model.Department;
import com.ritual.ems.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public void createDepartment(DepartmentRequest request) {
        validateDepartmentRequest(request);
        departmentRepository.save(request);
    }

    public void updateDepartment(Integer deptId, DepartmentRequest request) {
        validateDepartmentId(deptId);
        validateDepartmentRequest(request);

        int rows = departmentRepository.update(deptId, request);
        if (rows == 0) {
            throw new BusinessException("部门不存在");
        }
    }

    public void deleteDepartment(Integer deptId) {
        validateDepartmentId(deptId);
        if (departmentRepository.countEmployees(deptId) > 0) {
            throw new BusinessException("该部门下还有员工，不能删除");
        }

        int rows = departmentRepository.delete(deptId);
        if (rows == 0) {
            throw new BusinessException("部门不存在");
        }
    }

    private void validateDepartmentRequest(DepartmentRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getDeptName() == null || request.getDeptName().isBlank()) {
            throw new BusinessException("部门名称不能为空");
        }
        if (request.getDeptCode() == null || request.getDeptCode().isBlank()) {
            throw new BusinessException("部门编号不能为空");
        }
    }

    private void validateDepartmentId(Integer deptId) {
        if (deptId == null || deptId <= 0) {
            throw new BusinessException("部门ID不合法");
        }
    }
}
