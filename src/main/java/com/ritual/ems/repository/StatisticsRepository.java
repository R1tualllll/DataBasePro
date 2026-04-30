package com.ritual.ems.repository;

import com.ritual.ems.dto.response.MonthlyAttendanceResponse;
import com.ritual.ems.dto.response.StatisticsResponse;
import com.ritual.ems.dto.response.AttendanceIssueResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
@Repository
public class StatisticsRepository {
    private final JdbcTemplate jdbcTemplate;

    public StatisticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StatisticsResponse> countEmployeesByDepartment() {
        String sql = """
                select d.dept_id as id,
                       d.dept_name as name,
                       count(e.emp_id) as employee_count
                from joe.department d
                left join joe.employee e on d.dept_id = e.dept_id
                group by d.dept_id, d.dept_name
                order by d.dept_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new StatisticsResponse(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getLong("employee_count")
        ));
    }

    public List<StatisticsResponse> countEmployeesByPosition() {
        String sql = """
                select p.position_id as id,
                       p.position_name as name,
                       count(e.emp_id) as employee_count
                from joe.position p
                left join joe.employee e on p.position_id = e.position_id
                group by p.position_id, p.position_name
                order by p.position_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new StatisticsResponse(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getLong("employee_count")
        ));
    }

    public List<MonthlyAttendanceResponse> monthlyAttendance(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select e.emp_id,
                       e.emp_name,
                       d.dept_name,
                       coalesce(sum(case when a.attendance_status = 'present' then 1 else 0 end), 0) as present_days,
                       coalesce(sum(case when a.attendance_status = 'late' then 1 else 0 end), 0) as late_days,
                       coalesce(sum(case when a.attendance_status = 'leave' then 1 else 0 end), 0) as leave_days,
                       coalesce(sum(case when a.attendance_status = 'absent' then 1 else 0 end), 0) as absent_days,
                       coalesce(sum(a.late_minutes), 0) as total_late_minutes,
                       coalesce(sum(a.overtime_hours), 0) as total_overtime_hours
                from joe.employee e
                left join joe.department d on e.dept_id = d.dept_id
                left join joe.attendance a on e.emp_id = a.emp_id
                    and a.attendance_date >= ?
                    and a.attendance_date < ?
                group by e.emp_id, e.emp_name, d.dept_name
                order by e.emp_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MonthlyAttendanceResponse(
                rs.getInt("emp_id"),
                rs.getString("emp_name"),
                rs.getString("dept_name"),
                rs.getLong("present_days"),
                rs.getLong("late_days"),
                rs.getLong("leave_days"),
                rs.getLong("absent_days"),
                rs.getLong("total_late_minutes"),
                rs.getBigDecimal("total_overtime_hours")
        ), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
    }

    public long countMonthlyAttendanceIssues(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select count(*)
                from joe.attendance
                where attendance_date >= ?
                  and attendance_date < ?
                  and attendance_status in ('late', 'leave', 'absent')
                """;
        Long count = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                java.sql.Date.valueOf(startDate),
                java.sql.Date.valueOf(endDate)
        );
        return count == null ? 0L : count;
    }

    public List<AttendanceIssueResponse> monthlyAttendanceIssues(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select a.emp_id,
                       e.emp_name,
                       a.attendance_date,
                       a.attendance_status,
                       a.late_minutes
                from joe.attendance a
                join joe.employee e on a.emp_id = e.emp_id
                where a.attendance_date >= ?
                  and a.attendance_date < ?
                  and a.attendance_status in ('late', 'leave', 'absent')
                order by a.attendance_date desc, a.attendance_id desc
                limit 8
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AttendanceIssueResponse(
                rs.getInt("emp_id"),
                rs.getString("emp_name"),
                toLocalDate(rs.getTimestamp("attendance_date")),
                rs.getString("attendance_status"),
                (Integer) rs.getObject("late_minutes")
        ), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
    }

    public BigDecimal sumNetSalaryByMonth(String month) {
        String sql = """
                select coalesce(sum(net_salary), 0)
                from joe.salary
                where salary_month = ?
                """;
        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, month);
        return total == null ? BigDecimal.ZERO : total;
    }

    private LocalDate toLocalDate(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().toLocalDate();
    }
}
