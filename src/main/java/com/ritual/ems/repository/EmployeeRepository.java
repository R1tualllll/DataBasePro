package com.ritual.ems.repository;

import com.ritual.ems.dto.request.EmployeeRequest;
import com.ritual.ems.mapper.EmployeeRowMapper;
import com.ritual.ems.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

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

    private Date toSqlDate(java.time.LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
}
