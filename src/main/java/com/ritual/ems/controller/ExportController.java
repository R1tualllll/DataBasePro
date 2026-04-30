package com.ritual.ems.controller;

import com.ritual.ems.service.ExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/employees.csv")
    public ResponseEntity<String> exportEmployees() {
        return csv("employees.csv", exportService.exportEmployeesCsv());
    }

    @GetMapping("/departments.csv")
    public ResponseEntity<String> exportDepartments() {
        return csv("departments.csv", exportService.exportDepartmentsCsv());
    }

    @GetMapping("/positions.csv")
    public ResponseEntity<String> exportPositions() {
        return csv("positions.csv", exportService.exportPositionsCsv());
    }

    @GetMapping("/attendance.csv")
    public ResponseEntity<String> exportAttendance() {
        return csv("attendance.csv", exportService.exportAttendanceCsv());
    }

    @GetMapping("/monthly-attendance.csv")
    public ResponseEntity<String> exportMonthlyAttendance(@RequestParam("month") String month) {
        return csv("monthly-attendance-" + month + ".csv", exportService.exportMonthlyAttendanceCsv(month));
    }

    @GetMapping("/salaries.csv")
    public ResponseEntity<String> exportSalaries(@RequestParam("month") String month) {
        return csv("salaries-" + month + ".csv", exportService.exportSalariesCsv(month));
    }

    private ResponseEntity<String> csv(String filename, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());
        return ResponseEntity.ok().headers(headers).body(body);
    }
}
