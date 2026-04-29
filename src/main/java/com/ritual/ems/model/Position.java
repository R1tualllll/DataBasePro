package com.ritual.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {
    private Integer positionId;
    private String positionName;
    private String positionCode;
    private String level;
    private Double baseSalary;
    private String description;
}
