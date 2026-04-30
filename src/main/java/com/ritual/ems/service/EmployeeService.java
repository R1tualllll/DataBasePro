package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.EmployeeRequest;
import com.ritual.ems.dto.request.ResignRequest;
import com.ritual.ems.dto.response.EmployeeDetailResponse;
import com.ritual.ems.dto.response.PageResponse;
import com.ritual.ems.model.Department;
import com.ritual.ems.model.Employee;
import com.ritual.ems.model.Position;
import com.ritual.ems.repository.DepartmentRepository;
import com.ritual.ems.repository.EmployeeRepository;
import com.ritual.ems.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    private static final Set<String> VALID_GENDERS = Set.of("male", "female");
    private static final Set<String> VALID_STATUSES = Set.of("active", "resigned", "probation");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public PageResponse<Employee> searchEmployees(String keyword,
                                                  Integer deptId,
                                                  Integer positionId,
                                                  String status,
                                                  Integer page,
                                                  Integer pageSize) {
        int normalizedPage = normalizePage(page);
        int normalizedPageSize = normalizePageSize(pageSize);
        validateOptionalPositiveId(deptId, "部门ID不合法");
        validateOptionalPositiveId(positionId, "岗位ID不合法");
        validateOptionalStatus(status);

        long total = employeeRepository.countSearch(keyword, deptId, positionId, status);
        int totalPages = Math.max((int) Math.ceil((double) total / normalizedPageSize), 1);
        normalizedPage = Math.min(normalizedPage, totalPages);
        List<Employee> records = employeeRepository.search(
                keyword,
                deptId,
                positionId,
                status,
                normalizedPage,
                normalizedPageSize
        );
        return new PageResponse<>(records, total, normalizedPage, normalizedPageSize, totalPages);
    }

    public EmployeeDetailResponse getEmployeeDetail(Integer empId) {
        Employee employee = getExistingEmployee(empId);
        Department department = departmentRepository.findById(employee.getDeptId())
                .orElseThrow(() -> new BusinessException("员工部门不存在"));
        Position position = positionRepository.findById(employee.getPositionId())
                .orElseThrow(() -> new BusinessException("员工岗位不存在"));
        return new EmployeeDetailResponse(employee, department, position);
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
        if (employeeRepository.countAttendanceRecords(empId) > 0) {
            throw new BusinessException("该员工存在考勤记录，不能删除");
        }
        if (employeeRepository.countSalaryRecords(empId) > 0) {
            throw new BusinessException("该员工存在薪资记录，不能删除");
        }

        int rows = employeeRepository.delete(empId);
        if (rows == 0) {
            throw new BusinessException("员工不存在");
        }
    }

    public void regularizeEmployee(Integer empId) {
        Employee employee = getExistingEmployee(empId);
        if (!"probation".equals(employee.getStatus())) {
            throw new BusinessException("只有试用期员工可以转正");
        }
        employeeRepository.regularize(empId);
    }

    public void resignEmployee(Integer empId, ResignRequest request) {
        Employee employee = getExistingEmployee(empId);
        if (request == null || request.getLeaveDate() == null) {
            throw new BusinessException("离职日期不能为空");
        }
        LocalDate leaveDate = request.getLeaveDate();
        if (leaveDate.isBefore(employee.getHireDate())) {
            throw new BusinessException("离职日期不能早于入职日期");
        }
        if ("resigned".equals(employee.getStatus())) {
            throw new BusinessException("员工已离职");
        }
        employeeRepository.resign(empId, leaveDate);
    }

    public Employee getExistingEmployee(Integer empId) {
        validateEmployeeId(empId);
        return employeeRepository.findById(empId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
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
        if ("resigned".equals(request.getStatus()) && request.getLeaveDate() == null) {
            throw new BusinessException("离职员工必须填写离职日期");
        }
    }

    private void validateEmployeeId(Integer empId) {
        if (empId == null || empId <= 0) {
            throw new BusinessException("员工ID不合法");
        }
    }

    private void validateOptionalPositiveId(Integer id, String message) {
        if (id != null && id <= 0) {
            throw new BusinessException(message);
        }
    }

    private void validateOptionalStatus(String status) {
        if (status != null && !status.isBlank() && !VALID_STATUSES.contains(status)) {
            throw new BusinessException("员工状态只能是 active、resigned 或 probation");
        }
    }

    public int normalizePage(Integer page) {
        if (page == null) {
            return 1;
        }
        if (page <= 0) {
            throw new BusinessException("页码必须大于0");
        }
        return page;
    }

    public int normalizePageSize(Integer pageSize) {
        if (pageSize == null) {
            return 10;
        }
        if (pageSize <= 0 || pageSize > 100) {
            throw new BusinessException("每页条数必须在1到100之间");
        }
        return pageSize;
    }
}
