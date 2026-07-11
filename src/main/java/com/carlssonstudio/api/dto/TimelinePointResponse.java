package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TimelinePointResponse {
    private LocalDate date;
    private long count;
}