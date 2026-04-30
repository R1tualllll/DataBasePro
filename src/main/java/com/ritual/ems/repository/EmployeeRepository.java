package com.ritual.ems.repository;

import com.ritual.ems.dto.request.EmployeeRequest;
import com.ritual.ems.mapper.EmployeeRowMapper;
import com.ritual.ems.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Employee> findAll() {
        String sql = """
                select emp_id, emp_name, gender, age, phone, email,
                       hire_date, dept_id, position_id, status, leave_date
                from joe.employee
                order by emp_id
                """;
        return jdbcTemplate.query(sql, new EmployeeRowMapper());
    }

    public Optional<Employee> findById(Integer empId) {
        String sql = """
                select emp_id, emp_name, gender, age, phone, email,
                       hire_date, dept_id, position_id, status, leave_date
                from joe.employee
                where emp_id = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(), empId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Employee> search(String keyword,
                                 Integer deptId,
                                 Integer positionId,
                                 String status,
                                 int page,
                                 int pageSize) {
        List<Object> params = new ArrayList<>();
        String where = buildSearchWhere(keyword, deptId, positionId, status, params);
        int offset = (page - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);

        String sql = """
                select emp_id, emp_name, gender, age, phone, email,
                       hire_date, dept_id, position_id, status, leave_date
                from joe.employee
                """ + where + """

                order by emp_id
                limit ? offset ?
                """;
        return jdbcTemplate.query(sql, new EmployeeRowMapper(), params.toArray());
    }

    public long countSearch(String keyword, Integer deptId, Integer positionId, String status) {
        List<Object> params = new ArrayList<>();
        String where = buildSearchWhere(keyword, deptId, positionId, status, params);
        String sql = "select count(*) from joe.employee " + where;
        Long total = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return total == null ? 0L : total;
    }

    public List<Employee> findPayrollEligibleEmployees() {
        String sql = """
                select emp_id, emp_name, gender, age, phone, email,
                       hire_date, dept_id, position_id, status, leave_date
                from joe.employee
                where status in ('active', 'probation')
                order by emp_id
                """;
        return jdbcTemplate.query(sql, new EmployeeRowMapper());
    }

    public long countByStatus(String status) {
        String sql = """
                select count(*)
                from joe.employee
                where status = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count == null ? 0L : count;
    }

    public long countAttendanceRecords(Integer empId) {
        String sql = """
                select count(*)
                from joe.attendance
                where emp_id = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, empId);
        return count == null ? 0L : count;
    }

    public long countSalaryRecords(Integer empId) {
        String sql = """
                select count(*)
                from joe.salary
                where emp_id = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, empId);
        return count == null ? 0L : count;
    }

    public int save(EmployeeRequest request) {
        String sql = """
                insert into joe.employee (
                    emp_name, gender, age, phone, email,
                    hire_date, dept_id, position_id, status, leave_date
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                request.getEmpName(),
                request.getGender(),
                request.getAge(),
                request.getPhone(),
                request.getEmail(),
                toSqlDate(request.getHireDate()),
                request.getDeptId(),
                request.getPositionId(),
                request.getStatus(),
                toSqlDate(request.getLeaveDate())
        );
    }

    public int update(Integer empId, EmployeeRequest request) {
        String sql = """
                update joe.employee
                set emp_name = ?, gender = ?, age = ?, phone = ?, email = ?,
                    hire_date = ?, dept_id = ?, position_id = ?, status = ?, leave_date = ?
                where emp_id = ?
                """;
        return jdbcTemplate.update(
                sql,
                request.getEmpName(),
                request.getGender(),
                request.getAge(),
                request.getPhone(),
                request.getEmail(),
                toSqlDate(request.getHireDate()),
                request.getDeptId(),
                request.getPositionId(),
                request.getStatus(),
                toSqlDate(request.getLeaveDate()),
                empId
        );
    }

    public int delete(Integer empId) {
        String sql = """
                delete from joe.employee
                where emp_id = ?
                """;
        return jdbcTemplate.update(sql, empId);
    }

    public int regularize(Integer empId) {
        String sql = """
                update joe.employee
                set status = 'active', leave_date = null
                where emp_id = ?
                """;
        return jdbcTemplate.update(sql, empId);
    }

    public int resign(Integer empId, java.time.LocalDate leaveDate) {
        String sql = """
                update joe.employee
                set status = 'resigned', leave_date = ?
                where emp_id = ?
                """;
        return jdbcTemplate.update(sql, toSqlDate(leaveDate), empId);
    }

    private String buildSearchWhere(String keyword,
                                    Integer deptId,
                                    Integer positionId,
                                    String status,
                                    List<Object> params) {
        List<String> conditions = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            conditions.add("(lower(emp_name) like ? or lower(phone) like ? or lower(email) like ?)");
            String like = "%" + keyword.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (deptId != null) {
            conditions.add("dept_id = ?");
            params.add(deptId);
        }
        if (positionId != null) {
            conditions.add("position_id = ?");
            params.add(positionId);
        }
        if (status != null && !status.isBlank()) {
            conditions.add("status = ?");
            params.add(status);
        }
        return conditions.isEmpty() ? "" : " where " + String.join(" and ", conditions);
    }

    private Date toSqlDate(java.time.LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
}
