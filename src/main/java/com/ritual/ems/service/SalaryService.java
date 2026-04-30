package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.SalaryRequest;
import com.ritual.ems.dto.response.PageResponse;
import com.ritual.ems.dto.response.SalaryGenerateResponse;
import com.ritual.ems.model.Employee;
import com.ritual.ems.model.Salary;
import com.ritual.ems.repository.EmployeeRepository;
import com.ritual.ems.repository.PositionRepository;
import com.ritual.ems.repository.SalaryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalaryService {

    private static final String SALARY_MONTH_PATTERN = "\\d{4}-(0[1-9]|1[0-2])";

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    public SalaryService(SalaryRepository salaryRepository,
                         EmployeeRepository employeeRepository,
                         PositionRepository positionRepository) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    public PageResponse<Salary> searchSalaries(Integer empId, String month, Integer page, Integer pageSize) {
        validateOptionalEmployeeId(empId);
        validateOptionalSalaryMonth(month);
        int normalizedPage = normalizePage(page);
        int normalizedPageSize = normalizePageSize(pageSize);
        long total = salaryRepository.countSearch(empId, month);
        int totalPages = Math.max((int) Math.ceil((double) total / normalizedPageSize), 1);
        normalizedPage = Math.min(normalizedPage, totalPages);
        List<Salary> records = salaryRepository.search(empId, month, normalizedPage, normalizedPageSize);
        return new PageResponse<>(records, total, normalizedPage, normalizedPageSize, totalPages);
    }

    public void createSalary(SalaryRequest request) {
        validateSalaryRequest(request);
        applyCalculatedSalary(request);
        salaryRepository.save(request);
    }

    public void updateSalary(Integer salaryId, SalaryRequest request) {
        validateSalaryId(salaryId);
        validateSalaryRequest(request);
        applyCalculatedSalary(request);

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

    public SalaryGenerateResponse generateMonthlySalaries(String month) {
        validateSalaryMonth(month);
        List<Integer> generatedEmployeeIds = new ArrayList<>();
        List<Integer> skippedEmployeeIds = new ArrayList<>();

        for (Employee employee : employeeRepository.findPayrollEligibleEmployees()) {
            if (salaryRepository.existsByEmployeeAndMonth(employee.getEmpId(), month)) {
                skippedEmployeeIds.add(employee.getEmpId());
                continue;
            }
            SalaryRequest request = new SalaryRequest();
            request.setEmpId(employee.getEmpId());
            request.setSalaryMonth(month);
            request.setBonus(BigDecimal.ZERO);
            request.setDeduction(BigDecimal.ZERO);
            applyCalculatedSalary(request);
            salaryRepository.save(request);
            generatedEmployeeIds.add(employee.getEmpId());
        }

        return new SalaryGenerateResponse(
                generatedEmployeeIds.size(),
                skippedEmployeeIds.size(),
                generatedEmployeeIds,
                skippedEmployeeIds
        );
    }

    private void validateSalaryRequest(SalaryRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getEmpId() == null || request.getEmpId() <= 0) {
            throw new BusinessException("员工ID不合法");
        }
        validateSalaryMonth(request.getSalaryMonth());
        if (request.getBonus() == null) {
            request.setBonus(BigDecimal.ZERO);
        }
        if (request.getDeduction() == null) {
            request.setDeduction(BigDecimal.ZERO);
        }
        validateOptionalMoney(request.getBonus(), "奖金");
        validateOptionalMoney(request.getDeduction(), "扣款");
    }

    private void applyCalculatedSalary(SalaryRequest request) {
        Employee employee = employeeRepository.findById(request.getEmpId())
                .orElseThrow(() -> new BusinessException("员工不存在"));
        LocalDate salaryDate = YearMonth.parse(request.getSalaryMonth()).atEndOfMonth();
        validateEmployeeCanHaveBusinessRecord(employee, salaryDate);

        BigDecimal baseSalary = positionRepository.findBaseSalaryById(employee.getPositionId())
                .orElseThrow(() -> new BusinessException("员工岗位不存在"));
        BigDecimal netSalary = baseSalary
                .add(request.getBonus())
                .subtract(request.getDeduction());
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("实发工资不能为负数");
        }
        request.setBaseSalary(baseSalary);
        request.setNetSalary(netSalary);
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

    private void validateOptionalEmployeeId(Integer empId) {
        if (empId != null && empId <= 0) {
            throw new BusinessException("员工ID不合法");
        }
    }

    private void validateOptionalSalaryMonth(String month) {
        if (month != null && !month.isBlank()) {
            validateSalaryMonth(month);
        }
    }

    private void validateSalaryMonth(String month) {
        if (month == null || !month.matches(SALARY_MONTH_PATTERN)) {
            throw new BusinessException("薪资月份必须符合 yyyy-MM 格式");
        }
    }

    private int normalizePage(Integer page) {
        if (page == null) {
            return 1;
        }
        if (page <= 0) {
            throw new BusinessException("页码必须大于0");
        }
        return page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null) {
            return 10;
        }
        if (pageSize <= 0 || pageSize > 100) {
            throw new BusinessException("每页条数必须在1到100之间");
        }
        return pageSize;
    }

    private void validateEmployeeCanHaveBusinessRecord(Employee employee, LocalDate businessDate) {
        if ("resigned".equals(employee.getStatus())
                && employee.getLeaveDate() != null
                && businessDate.isAfter(employee.getLeaveDate())) {
            throw new BusinessException("员工已离职，不能维护离职日期后的薪资记录");
        }
    }
}
