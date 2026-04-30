package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.AttendanceRequest;
import com.ritual.ems.dto.response.PageResponse;
import com.ritual.ems.model.Attendance;
import com.ritual.ems.model.Employee;
import com.ritual.ems.repository.AttendanceRepository;
import com.ritual.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class AttendanceService {

    private static final Set<String> VALID_ATTENDANCE_STATUSES = Set.of("present", "late", "leave", "absent");

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public PageResponse<Attendance> searchAttendance(Integer empId,
                                                     String month,
                                                     String status,
                                                     LocalDate startDate,
                                                     LocalDate endDate,
                                                     Integer page,
                                                     Integer pageSize) {
        validateOptionalEmployeeId(empId);
        validateOptionalMonth(month);
        validateOptionalStatus(status);
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        int normalizedPage = normalizePage(page);
        int normalizedPageSize = normalizePageSize(pageSize);
        long total = attendanceRepository.countSearch(empId, month, status, startDate, endDate);
        int totalPages = Math.max((int) Math.ceil((double) total / normalizedPageSize), 1);
        normalizedPage = Math.min(normalizedPage, totalPages);
        List<Attendance> records = attendanceRepository.search(
                empId,
                month,
                status,
                startDate,
                endDate,
                normalizedPage,
                normalizedPageSize
        );
        return new PageResponse<>(records, total, normalizedPage, normalizedPageSize, totalPages);
    }

    public void createAttendance(AttendanceRequest request) {
        validateAttendanceRequest(request);
        validateEmployeeCanHaveBusinessRecord(request);
        attendanceRepository.save(request);
    }

    public void updateAttendance(Integer attendanceId, AttendanceRequest request) {
        validateAttendanceId(attendanceId);
        validateAttendanceRequest(request);
        validateEmployeeCanHaveBusinessRecord(request);

        int rows = attendanceRepository.update(attendanceId, request);
        if (rows == 0) {
            throw new BusinessException("考勤记录不存在");
        }
    }

    public void deleteAttendance(Integer attendanceId) {
        validateAttendanceId(attendanceId);

        int rows = attendanceRepository.delete(attendanceId);
        if (rows == 0) {
            throw new BusinessException("考勤记录不存在");
        }
    }

    private void validateAttendanceRequest(AttendanceRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getEmpId() == null || request.getEmpId() <= 0) {
            throw new BusinessException("员工ID不合法");
        }
        if (request.getAttendanceDate() == null) {
            throw new BusinessException("考勤日期不能为空");
        }
        if (request.getAttendanceStatus() == null || request.getAttendanceStatus().isBlank()) {
            throw new BusinessException("考勤状态不能为空");
        }
        if (!VALID_ATTENDANCE_STATUSES.contains(request.getAttendanceStatus())) {
            throw new BusinessException("考勤状态只能是 present、late、leave 或 absent");
        }
        if (request.getLateMinutes() == null) {
            request.setLateMinutes(0);
        }
        if (request.getOvertimeHours() == null) {
            request.setOvertimeHours(BigDecimal.ZERO);
        }
        if (request.getLateMinutes() != null && request.getLateMinutes() < 0) {
            throw new BusinessException("迟到分钟数不能为负数");
        }
        if (request.getOvertimeHours() != null && request.getOvertimeHours().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("加班小时数不能为负数");
        }
    }

    private void validateAttendanceId(Integer attendanceId) {
        if (attendanceId == null || attendanceId <= 0) {
            throw new BusinessException("考勤ID不合法");
        }
    }

    private void validateOptionalEmployeeId(Integer empId) {
        if (empId != null && empId <= 0) {
            throw new BusinessException("员工ID不合法");
        }
    }

    private void validateOptionalMonth(String month) {
        if (month != null && !month.isBlank() && !month.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new BusinessException("考勤月份必须符合 yyyy-MM 格式");
        }
    }

    private void validateOptionalStatus(String status) {
        if (status != null && !status.isBlank() && !VALID_ATTENDANCE_STATUSES.contains(status)) {
            throw new BusinessException("考勤状态只能是 present、late、leave 或 absent");
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

    private void validateEmployeeCanHaveBusinessRecord(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmpId())
                .orElseThrow(() -> new BusinessException("员工不存在"));
        if ("resigned".equals(employee.getStatus())
                && employee.getLeaveDate() != null
                && request.getAttendanceDate().isAfter(employee.getLeaveDate())) {
            throw new BusinessException("员工已离职，不能维护离职日期后的考勤记录");
        }
    }
}
