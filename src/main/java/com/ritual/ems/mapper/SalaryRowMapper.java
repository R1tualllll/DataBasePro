package com.ritual.ems.mapper;

import com.ritual.ems.model.Salary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryRowMapper implements RowMapper<Salary> {

    @Override
    public Salary mapRow(ResultSet rs, int rowNum) throws SQLException {
        Salary salary = new Salary();
        salary.setSalaryId(rs.getInt("salary_id"));
        salary.setEmpId(rs.getInt("emp_id"));
        salary.setSalaryMonth(rs.getString("salary_month"));
        salary.setBaseSalary(rs.getBigDecimal("base_salary"));
        salary.setBonus(rs.getBigDecimal("bonus"));
        salary.setDeduction(rs.getBigDecimal("deduction"));
        salary.setNetSalary(rs.getBigDecimal("net_salary"));
        return salary;
    }
}
