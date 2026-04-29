package com.ritual.ems.repository;

import com.ritual.ems.model.Department;
import com.ritual.ems.mapper.DepartmentRowMapper;
import com.ritual.ems.dto.request.DepartmentRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentRepository {
    private final JdbcTemplate jdbcTemplate;

    public DepartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Department> findAll() {
        String sql = """
                select dept_id, dept_name, dept_code, manager_name, phone, location
                from joe.department
                order by dept_id
                """;
        return jdbcTemplate.query(sql, new DepartmentRowMapper());
    }

    public int save(DepartmentRequest request) {
        String sql = """
                insert into joe.department(dept_name, dept_code, manager_name, phone, location)
                values(?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                request.getDeptName(),
                request.getDeptCode(),
                request.getManagerName(),
                request.getPhone(),
                request.getLocation()
        );
    }

    public int update(Integer deptId, DepartmentRequest request) {
        String sql = """
            update joe.department
            set dept_name = ?, dept_code = ?, manager_name = ?, phone = ?, location = ?
            where dept_id = ?
            """;
        return jdbcTemplate.update(
                sql,
                request.getDeptName(),
                request.getDeptCode(),
                request.getManagerName(),
                request.getPhone(),
                request.getLocation(),
                deptId
        );
    }

    public int delete(Integer deptId) {
        String sql = """
            delete from joe.department
            where dept_id = ?
            """;
        return jdbcTemplate.update(sql, deptId);
    }
}
