package com.ritual.ems.repository;

import com.ritual.ems.dto.response.StatisticsResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
