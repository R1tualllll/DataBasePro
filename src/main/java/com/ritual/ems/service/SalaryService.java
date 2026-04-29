package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.SalaryRequest;
import com.ritual.ems.model.Salary;
import com.ritual.ems.repository.SalaryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SalaryService {

    private static final String SALARY_MONTH_PATTERN = "\\d{4}-(0[1-9]|1[0-2])";

    private final SalaryRepository salaryRepository;

    public SalaryService(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }

    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    public void createSalary(SalaryRequest request) {
        validateSalaryRequest(request);
        salaryRepository.save(request);
    }

    public void updateSalary(Integer salaryId, SalaryRequest request) {
        validateSalaryId(salaryId);
        validateSalaryRequest(request);

        int rows = salaryRepository.update(salaryId, request);
        if (rows == 0) {
            throw new BusinessException("薪资记录不存在");
        }
    }

    public void deleteSalary(Integer salaryId) {
        validateSalaryId(salaryId);

        int rows = salaryRepository.delete(salaryId);
        if (rows == 0) {
            throw new BusinessException("薪资记录不存在");
        }
    }

    private void validateSalaryRequest(SalaryRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getEmpId() == null || request.getEmpId() <= 0) {
            throw new BusinessException("员工ID不合法");
        }
        if (request.getSalaryMonth() == null || !request.getSalaryMonth().matches(SALARY_MONTH_PATTERN)) {
            throw new BusinessException("薪资月份必须符合 yyyy-MM 格式");
        }
        if (request.getBonus() == null) {
            request.setBonus(BigDecimal.ZERO);
        }
        if (request.getDeduction() == null) {
            request.setDeduction(BigDecimal.ZERO);
        }
        validateRequiredMoney(request.getBaseSalary(), "基础工资");
        validateOptionalMoney(request.getBonus(), "奖金");
        validateOptionalMoney(request.getDeduction(), "扣款");
        validateRequiredMoney(request.getNetSalary(), "实发工资");
    }

    private void validateRequiredMoney(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + "不能为空");
        }
        validateOptionalMoney(value, fieldName);
    }

    private void validateOptionalMoney(BigDecimal value, String fieldName) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(fieldName + "不能为负数");
        }
    }

    private void validateSalaryId(Integer salaryId) {
        if (salaryId == null || salaryId <= 0) {
            throw new BusinessException("薪资ID不合法");
        }
    }
}
