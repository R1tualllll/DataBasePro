package com.ritual.ems.repository;

import com.ritual.ems.dto.request.SalaryRequest;
import com.ritual.ems.mapper.SalaryRowMapper;
import com.ritual.ems.model.Salary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SalaryRepository {

    private final JdbcTemplate jdbcTemplate;

    public SalaryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Salary> findAll() {
        String sql = """
                select salary_id, emp_id, salary_month, base_salary,
                       bonus, deduction, net_salary
                from joe.salary
                order by salary_id
                """;
        return jdbcTemplate.query(sql, new SalaryRowMapper());
    }

    public int save(SalaryRequest request) {
        String sql = """
                insert into joe.salary (
                    emp_id, salary_month, base_salary, bonus, deduction, net_salary
                )
                values (?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                request.getEmpId(),
                request.getSalaryMonth(),
                request.getBaseSalary(),
                request.getBonus(),
                request.getDeduction(),
                request.getNetSalary()
        );
    }

    public int update(Integer salaryId, SalaryRequest request) {
        String sql = """
                update joe.salary
                set emp_id = ?, salary_month = ?, base_salary = ?, bonus = ?, deduction = ?, net_salary = ?
                where salary_id = ?
                """;
        return jdbcTemplate.update(
                sql,
                request.getEmpId(),
                request.getSalaryMonth(),
                request.getBaseSalary(),
                request.getBonus(),
                request.getDeduction(),
                request.getNetSalary(),
                salaryId
        );
    }

    public int delete(Integer salaryId) {
        String sql = """
                delete from joe.salary
                where salary_id = ?
                """;
        return jdbcTemplate.update(sql, salaryId);
    }
}
