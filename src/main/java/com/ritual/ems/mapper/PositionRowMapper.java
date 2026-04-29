package com.ritual.ems.mapper;

import com.ritual.ems.model.Position;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PositionRowMapper implements RowMapper<Position>{
    @Override
    public Position mapRow(ResultSet rs, int rowNum) throws SQLException {
        Position position = new Position();
        position.setPositionId(rs.getInt("position_id"));
        position.setPositionName(rs.getString("position_name"));
        position.setPositionCode(rs.getString("position_code"));
        position.setLevel(rs.getString("level"));
        position.setBaseSalary(rs.getDouble("base_salary"));
        position.setDescription(rs.getString("description"));
        return position;
    }
}
