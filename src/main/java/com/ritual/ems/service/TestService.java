package com.ritual.ems.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TestService {

    private final JdbcTemplate jdbcTemplate;

    public TestService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> testDatabaseConnection() {
        Integer ping = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ping", ping);
        result.put("databaseName", jdbcTemplate.queryForObject("SELECT current_database()", String.class));
        result.put("schemaName", jdbcTemplate.queryForObject("SELECT current_schema()", String.class));
        return result;
    }
}
