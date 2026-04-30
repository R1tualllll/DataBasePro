package com.ritual.ems.service;

import com.ritual.ems.common.exception.BusinessException;
import com.ritual.ems.dto.request.PositionRequest;
import com.ritual.ems.model.Position;
import com.ritual.ems.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public void createPosition(PositionRequest request) {
        validatePositionRequest(request);
        positionRepository.save(request);
    }

    public void updatePosition(Integer positionId, PositionRequest request) {
        validatePositionId(positionId);
        validatePositionRequest(request);

        int rows = positionRepository.update(positionId, request);
        if (rows == 0) {
            throw new BusinessException("岗位不存在");
        }
    }

    public void deletePosition(Integer positionId) {
        validatePositionId(positionId);
        if (positionRepository.countEmployees(positionId) > 0) {
            throw new BusinessException("该岗位下还有员工，不能删除");
        }

        int rows = positionRepository.delete(positionId);
        if (rows == 0) {
            throw new BusinessException("岗位不存在");
        }
    }

    private void validatePositionRequest(PositionRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getPositionName() == null || request.getPositionName().isBlank()) {
            throw new BusinessException("岗位名称不能为空");
        }
        if (request.getPositionCode() == null || request.getPositionCode().isBlank()) {
            throw new BusinessException("岗位编号不能为空");
        }
        if (request.getBaseSalary() == null) {
            throw new BusinessException("基础工资不能为空");
        }
        if (request.getBaseSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("基础工资不能为负数");
        }
    }

    private void validatePositionId(Integer positionId) {
        if (positionId == null || positionId <= 0) {
            throw new BusinessException("岗位ID不合法");
        }
    }
}
