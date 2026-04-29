package com.ritual.ems.repository;

import com.ritual.ems.dto.request.PositionRequest;
import com.ritual.ems.model.Position;
import com.ritual.ems.mapper.PositionRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PositionRepository {
    private final JdbcTemplate jdbcTemplate;

    public PositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Position> findAll() {
        String sql = """
                        select position_id, position_name, position_code, level, base_salary, description
                        from joe.position
                        order by position_id
                """;
        return jdbcTemplate.query(sql, new PositionRowMapper());
    }

    public int save(PositionRequest request) {
        String sql = """
                insert into joe.position (position_name, position_code, level, base_salary, description)
                values (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                request.getPositionName(),
                request.getPositionCode(),
                request.getLevel(),
                request.getBaseSalary(),
                request.getDescription()
        );
    }

    public int update(Integer positionId, PositionRequest request) {
        String sql = """
                update joe.position
                set position_name = ?, position_code = ?, level = ?, base_salary = ?, description = ?
                where position_id = ?
                """;
        return jdbcTemplate.update(
                sql,
                request.getPositionName(),
                request.getPositionCode(),
                request.getLevel(),
                request.getBaseSalary(),
                request.getDescription(),
                positionId
        );
    }

    public int delete(Integer positionId) {
        String sql = """
                delete from joe.position
                where position_id = ?
                """;
        return jdbcTemplate.update(sql, positionId);
    }
}
