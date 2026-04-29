package com.ritual.ems.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
    private Integer id;
    private String name;
    private Long employeeCount;
}
