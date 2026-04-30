package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.response.DashboardResponse;
import com.ritual.ems.dto.response.MonthlyAttendanceResponse;
import com.ritual.ems.dto.response.StatisticsResponse;
import com.ritual.ems.repository.EmployeeRepository;
import com.ritual.ems.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final EmployeeRepository employeeRepository;

    public StatisticsService(StatisticsRepository statisticsRepository, EmployeeRepository employeeRepository) {
        this.statisticsRepository = statisticsRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<StatisticsResponse> countEmployeesByDepartment() {
        return statisticsRepository.countEmployeesByDepartment();
    }

    public List<StatisticsResponse> countEmployeesByPosition() {
        return statisticsRepository.countEmployeesByPosition();
    }

    public List<MonthlyAttendanceResponse> monthlyAttendance(String month) {
        validateMonth(month);
        YearMonth yearMonth = YearMonth.parse(month);
        return statisticsRepository.monthlyAttendance(
                yearMonth.atDay(1),
                yearMonth.plusMonths(1).atDay(1)
        );
    }

    public DashboardResponse dashboard(String month) {
        validateMonth(month);
        YearMonth yearMonth = YearMonth.parse(month);
        return new DashboardResponse(
                employeeRepository.countByStatus("active"),
                employeeRepository.countByStatus("probation"),
                statisticsRepository.countMonthlyAttendanceIssues(yearMonth.atDay(1), yearMonth.plusMonths(1).atDay(1)),
                statisticsRepository.sumNetSalaryByMonth(month),
                statisticsRepository.countEmployeesByDepartment(),
                statisticsRepository.monthlyAttendanceIssues(yearMonth.atDay(1), yearMonth.plusMonths(1).atDay(1))
        );
    }

    private void validateMonth(String month) {
        if (month == null || !month.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new BusinessException("统计月份必须符合 yyyy-MM 格式");
        }
    }
}
