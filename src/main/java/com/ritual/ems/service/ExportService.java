package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.response.MonthlyAttendanceResponse;
import com.ritual.ems.model.Attendance;
import com.ritual.ems.model.Department;
import com.ritual.ems.model.Employee;
import com.ritual.ems.model.Position;
import com.ritual.ems.model.Salary;
import com.ritual.ems.repository.AttendanceRepository;
import com.ritual.ems.repository.DepartmentRepository;
import com.ritual.ems.repository.EmployeeRepository;
import com.ritual.ems.repository.PositionRepository;
import com.ritual.ems.repository.SalaryRepository;
import com.ritual.ems.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class ExportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryRepository salaryRepository;
    private final StatisticsRepository statisticsRepository;

    public ExportService(EmployeeRepository employeeRepository,
                         DepartmentRepository departmentRepository,
                         PositionRepository positionRepository,
                         AttendanceRepository attendanceRepository,
                         SalaryRepository salaryRepository,
                         StatisticsRepository statisticsRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.attendanceRepository = attendanceRepository;
        this.salaryRepository = salaryRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public String exportDepartmentsCsv() {
        StringBuilder csv = newCsv("部门ID,部门名称,部门编号,负责人,联系电话,办公地点");
        for (Department department : departmentRepository.findAll()) {
            appendRow(csv,
                    department.getDeptId(),
                    department.getDeptName(),
                    department.getDeptCode(),
                    department.getManagerName(),
                    department.getPhone(),
                    department.getLocation());
        }
        return csv.toString();
    }

    public String exportPositionsCsv() {
        StringBuilder csv = newCsv("岗位ID,岗位名称,岗位编号,岗位级别,基础工资,岗位描述");
        for (Position position : positionRepository.findAll()) {
            appendRow(csv,
                    position.getPositionId(),
                    position.getPositionName(),
                    position.getPositionCode(),
                    position.getLevel(),
                    position.getBaseSalary(),
                    position.getDescription());
        }
        return csv.toString();
    }

    public String exportEmployeesCsv() {
        StringBuilder csv = newCsv("员工ID,姓名,性别,年龄,手机号,邮箱,入职日期,部门ID,岗位ID,状态,离职日期");
        for (Employee employee : employeeRepository.findAll()) {
            appendRow(csv,
                    employee.getEmpId(),
                    employee.getEmpName(),
                    employee.getGender(),
                    employee.getAge(),
                    employee.getPhone(),
                    employee.getEmail(),
                    employee.getHireDate(),
                    employee.getDeptId(),
                    employee.getPositionId(),
                    employee.getStatus(),
                    employee.getLeaveDate());
        }
        return csv.toString();
    }

    public String exportMonthlyAttendanceCsv(String month) {
        validateMonth(month);
        YearMonth yearMonth = YearMonth.parse(month);
        List<MonthlyAttendanceResponse> rows = statisticsRepository.monthlyAttendance(
                yearMonth.atDay(1),
                yearMonth.plusMonths(1).atDay(1)
        );
        StringBuilder csv = newCsv("员工ID,姓名,部门,出勤天数,迟到天数,请假天数,缺勤天数,迟到总分钟,加班总小时");
        for (MonthlyAttendanceResponse row : rows) {
            appendRow(csv,
                    row.getEmpId(),
                    row.getEmpName(),
                    row.getDeptName(),
                    row.getPresentDays(),
                    row.getLateDays(),
                    row.getLeaveDays(),
                    row.getAbsentDays(),
                    row.getTotalLateMinutes(),
                    row.getTotalOvertimeHours());
        }
        return csv.toString();
    }

    public String exportAttendanceCsv() {
        StringBuilder csv = newCsv("考勤ID,员工ID,考勤日期,考勤状态,迟到分钟,加班小时,备注");
        for (Attendance attendance : attendanceRepository.findAll()) {
            appendRow(csv,
                    attendance.getAttendanceId(),
                    attendance.getEmpId(),
                    attendance.getAttendanceDate(),
                    attendance.getAttendanceStatus(),
                    attendance.getLateMinutes(),
                    attendance.getOvertimeHours(),
                    attendance.getRemark());
        }
        return csv.toString();
    }

    public String exportSalariesCsv(String month) {
        validateMonth(month);
        StringBuilder csv = newCsv("薪资ID,员工ID,月份,基础工资,奖金,扣款,实发工资");
        for (Salary salary : salaryRepository.search(null, month, 1, 100000)) {
            appendRow(csv,
                    salary.getSalaryId(),
                    salary.getEmpId(),
                    salary.getSalaryMonth(),
                    salary.getBaseSalary(),
                    salary.getBonus(),
                    salary.getDeduction(),
                    salary.getNetSalary());
        }
        return csv.toString();
    }

    private StringBuilder newCsv(String header) {
        return new StringBuilder("\uFEFF").append(header).append("\n");
    }

    private void appendRow(StringBuilder csv, Object... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                csv.append(',');
            }
            csv.append(escape(values[i]));
        }
        csv.append('\n');
    }

    private String escape(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private void validateMonth(String month) {
        if (month == null || !month.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new BusinessException("导出月份必须符合 yyyy-MM 格式");
        }
    }
}
