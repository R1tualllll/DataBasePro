package com.ritual.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> records;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
