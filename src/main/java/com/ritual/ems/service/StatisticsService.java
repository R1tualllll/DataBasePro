package com.ritual.ems.service;

import com.ritual.ems.dto.response.StatisticsResponse;
import com.ritual.ems.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public List<StatisticsResponse> countEmployeesByDepartment() {
        return statisticsRepository.countEmployeesByDepartment();
    }

    public List<StatisticsResponse> countEmployeesByPosition() {
        return statisticsRepository.countEmployeesByPosition();
    }
}
