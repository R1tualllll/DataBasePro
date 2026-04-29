package com.ritual.ems.mapper;

import com.ritual.ems.model.Department;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentRowMapper implements RowMapper<Department>{
    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        Department department = new Department();
        department.setDeptId(rs.getInt("dept_id"));
        department.setDeptName(rs.getString("dept_name"));
        department.setDeptCode(rs.getString("dept_code"));
        department.setManagerName(rs.getString("manager_name"));
        department.setPhone(rs.getString("phone"));
        department.setLocation(rs.getString("location"));
        return department;
    }


}
