package com.ritual.ems.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {
    private String positionName;
    private String positionCode;
    private String level;
    private BigDecimal baseSalary;
    private String description;
}
