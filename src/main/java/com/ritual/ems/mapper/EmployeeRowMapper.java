package com.ritual.ems.mapper;

import com.ritual.ems.model.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(rs.getInt("emp_id"));
        employee.setEmpName(rs.getString("emp_name"));
        employee.setGender(rs.getString("gender"));
        employee.setAge((Integer) rs.getObject("age"));
        employee.setPhone(rs.getString("phone"));
        employee.setEmail(rs.getString("email"));
        employee.setHireDate(toLocalDate(rs.getTimestamp("hire_date")));
        employee.setDeptId(rs.getInt("dept_id"));
        employee.setPositionId(rs.getInt("position_id"));
        employee.setStatus(rs.getString("status"));
        employee.setLeaveDate(toLocalDate(rs.getTimestamp("leave_date")));
        return employee;
    }

    private LocalDate toLocalDate(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().toLocalDate();
    }
}
