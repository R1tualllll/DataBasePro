package com.ritual.ems.repository;

import com.ritual.ems.dto.request.SalaryRequest;
import com.ritual.ems.mapper.SalaryRowMapper;
import com.ritual.ems.model.Salary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public List<Salary> search(Integer empId, String month, int page, int pageSize) {
        List<Object> params = new ArrayList<>();
        String where = buildSearchWhere(empId, month, params);
        int offset = (page - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);
        String sql = """
                select salary_id, emp_id, salary_month, base_salary,
                       bonus, deduction, net_salary
                from joe.salary
                """ + where + """

                order by salary_month desc, salary_id desc
                limit ? offset ?
                """;
        return jdbcTemplate.query(sql, new SalaryRowMapper(), params.toArray());
    }

    public long countSearch(Integer empId, String month) {
        List<Object> params = new ArrayList<>();
        String where = buildSearchWhere(empId, month, params);
        String sql = "select count(*) from joe.salary " + where;
        Long total = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return total == null ? 0L : total;
    }

    public boolean existsByEmployeeAndMonth(Integer empId, String salaryMonth) {
        String sql = """
                select count(*)
                from joe.salary
                where emp_id = ? and salary_month = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, empId, salaryMonth);
        return count != null && count > 0;
    }

    public BigDecimal sumNetSalaryByMonth(String month) {
        String sql = """
                select coalesce(sum(net_salary), 0)
                from joe.salary
                where salary_month = ?
                """;
        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, month);
        return total == null ? BigDecimal.ZERO : total;
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

    private String buildSearchWhere(Integer empId, String month, List<Object> params) {
        List<String> conditions = new ArrayList<>();
        if (empId != null) {
            conditions.add("emp_id = ?");
            params.add(empId);
        }
        if (month != null && !month.isBlank()) {
            conditions.add("salary_month = ?");
            params.add(month);
        }
        return conditions.isEmpty() ? "" : " where " + String.join(" and ", conditions);
    }
}
