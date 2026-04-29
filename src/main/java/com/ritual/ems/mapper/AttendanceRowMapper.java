package com.ritual.ems.mapper;

import com.ritual.ems.model.Attendance;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class AttendanceRowMapper implements RowMapper<Attendance> {

    @Override
    public Attendance mapRow(ResultSet rs, int rowNum) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setEmpId(rs.getInt("emp_id"));
        attendance.setAttendanceDate(toLocalDate(rs.getTimestamp("attendance_date")));
        attendance.setAttendanceStatus(rs.getString("attendance_status"));
        attendance.setLateMinutes((Integer) rs.getObject("late_minutes"));
        attendance.setOvertimeHours(rs.getBigDecimal("overtime_hours"));
        attendance.setRemark(rs.getString("remark"));
        return attendance;
    }

    private LocalDate toLocalDate(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().toLocalDate();
    }
}
