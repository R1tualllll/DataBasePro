package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.AttendanceRequest;
import com.ritual.ems.model.Attendance;
import com.ritual.ems.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class AttendanceService {

    private static final Set<String> VALID_ATTENDANCE_STATUSES = Set.of("present", "late", "leave", "absent");

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public void createAttendance(AttendanceRequest request) {
        validateAttendanceRequest(request);
        attendanceRepository.save(request);
    }

    public void updateAttendance(Integer attendanceId, AttendanceRequest request) {
        validateAttendanceId(attendanceId);
        validateAttendanceRequest(request);

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
}
