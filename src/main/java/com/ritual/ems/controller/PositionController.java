package com.ritual.ems.controller;

import com.ritual.ems.common.Result;
import com.ritual.ems.dto.request.PositionRequest;
import com.ritual.ems.model.Position;
import com.ritual.ems.service.PositionService;
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
@RequestMapping({"/api/position", "/api/positions"})
public class PositionController {
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public Result<List<Position>> getAllPositions() {
        return Result.success(positionService.getAllPositions());
    }

    @PostMapping
    public Result<String> createPosition(@RequestBody PositionRequest request) {
        positionService.createPosition(request);
        return Result.success("Position created successfully");
    }

    @PutMapping("/{positionId}")
    public Result<String> updatePosition(@PathVariable("positionId") Integer positionId,
                                         @RequestBody PositionRequest request) {
        positionService.updatePosition(positionId, request);
        return Result.success("Position updated successfully");
    }

    @DeleteMapping("/{positionId}")
    public Result<String> deletePosition(@PathVariable("positionId") Integer positionId) {
        positionService.deletePosition(positionId);
        return Result.success("Position deleted successfully");
    }
}
