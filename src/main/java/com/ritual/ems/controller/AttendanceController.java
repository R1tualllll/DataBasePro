package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.dto.request.AttendanceRequest;
import com.ritual.ems.model.Attendance;
import com.ritual.ems.service.AttendanceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public Result<List<Attendance>> getAllAttendance() {
        return Result.success(attendanceService.getAllAttendance());
    }

    @PostMapping
    public Result<String> createAttendance(@RequestBody AttendanceRequest request) {
        attendanceService.createAttendance(request);
        return Result.success("Attendance created successfully");
    }

    @PutMapping("/{attendanceId}")
    public Result<String> updateAttendance(@PathVariable("attendanceId") Integer attendanceId,
                                           @RequestBody AttendanceRequest request) {
        attendanceService.updateAttendance(attendanceId, request);
        return Result.success("Attendance updated successfully");
    }

    @DeleteMapping("/{attendanceId}")
    public Result<String> deleteAttendance(@PathVariable("attendanceId") Integer attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return Result.success("Attendance deleted successfully");
    }
}
