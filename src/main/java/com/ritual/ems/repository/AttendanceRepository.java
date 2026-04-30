package com.ritual.ems.repository;

import com.ritual.ems.dto.request.AttendanceRequest;
import com.ritual.ems.mapper.AttendanceRowMapper;
import com.ritual.ems.model.Attendance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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

    public List<Attendance> search(Integer empId,
                                   String month,
                                   String status,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   int page,
                                   int pageSize) {
        List<Object> params = new ArrayList<>();
        String where = buildSearchWhere(empId, month, status, startDate, endDate, params);
        int offset = (page - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);
        String sql = """
                select attendance_id, emp_id, attendance_date, attendance_status,
                       late_minutes, overtime_hours, remark
                from joe.attendance
                """ + where + """

                order by attendance_date desc, attendance_id desc
                limit ? offset ?
                """;
        return jdbcTemplate.query(sql, new AttendanceRowMapper(), params.toArray());
    }

    public long countSearch(Integer empId,
                            String month,
                            String status,
                            LocalDate startDate,
                            LocalDate endDate) {
        List<Object> params = new ArrayList<>();
        String where = buildSearchWhere(empId, month, status, startDate, endDate, params);
        String sql = "select count(*) from joe.attendance " + where;
        Long total = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return total == null ? 0L : total;
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

    private String buildSearchWhere(Integer empId,
                                    String month,
                                    String status,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    List<Object> params) {
        List<String> conditions = new ArrayList<>();
        if (empId != null) {
            conditions.add("emp_id = ?");
            params.add(empId);
        }
        if (month != null && !month.isBlank()) {
            YearMonth yearMonth = YearMonth.parse(month);
            conditions.add("attendance_date >= ? and attendance_date < ?");
            params.add(Date.valueOf(yearMonth.atDay(1)));
            params.add(Date.valueOf(yearMonth.plusMonths(1).atDay(1)));
        } else {
            if (startDate != null) {
                conditions.add("attendance_date >= ?");
                params.add(Date.valueOf(startDate));
            }
            if (endDate != null) {
                conditions.add("attendance_date <= ?");
                params.add(Date.valueOf(endDate));
            }
        }
        if (status != null && !status.isBlank()) {
            conditions.add("attendance_status = ?");
            params.add(status);
        }
        return conditions.isEmpty() ? "" : " where " + String.join(" and ", conditions);
    }
}
