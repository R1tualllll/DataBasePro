package com.ritual.ems.repository;

import com.ritual.ems.dto.request.AttendanceRequest;
import com.ritual.ems.mapper.AttendanceRowMapper;
import com.ritual.ems.model.Attendance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class AttendanceRepository {

    private final JdbcTemplate jdbcTemplate;

    public AttendanceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Attendance> findAll() {
        String sql = """
                select attendance_id, emp_id, attendance_date, attendance_status,
                       late_minutes, overtime_hours, remark
                from joe.attendance
                order by attendance_id
                """;
        return jdbcTemplate.query(sql, new AttendanceRowMapper());
    }

    public int save(AttendanceRequest request) {
        String sql = """
                insert into joe.attendance (
                    emp_id, attendance_date, attendance_status,
                    late_minutes, overtime_hours, remark
                )
                values (?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                request.getEmpId(),
                toSqlDate(request.getAttendanceDate()),
                request.getAttendanceStatus(),
                request.getLateMinutes(),
                request.getOvertimeHours(),
                request.getRemark()
        );
    }

    public int update(Integer attendanceId, AttendanceRequest request) {
        String sql = """
                update joe.attendance
                set emp_id = ?, attendance_date = ?, attendance_status = ?,
                    late_minutes = ?, overtime_hours = ?, remark = ?
                where attendance_id = ?
                """;
        return jdbcTemplate.update(
                sql,
                request.getEmpId(),
                toSqlDate(request.getAttendanceDate()),
                request.getAttendanceStatus(),
                request.getLateMinutes(),
                request.getOvertimeHours(),
                request.getRemark(),
                attendanceId
        );
    }

    public int delete(Integer attendanceId) {
        String sql = """
                delete from joe.attendance
                where attendance_id = ?
                """;
        return jdbcTemplate.update(sql, attendanceId);
    }

    private Date toSqlDate(java.time.LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
}
