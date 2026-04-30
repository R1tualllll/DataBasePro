package com.ritual.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryGenerateResponse {
    private Integer generatedCount;
    private Integer skippedCount;
    private List<Integer> generatedEmployeeIds;
    private List<Integer> skippedEmployeeIds;
}
