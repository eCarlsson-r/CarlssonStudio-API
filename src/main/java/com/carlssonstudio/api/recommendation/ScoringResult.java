package com.carlssonstudio.api.recommendation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoringResult {
    private Foundation foundation;
    private int score;
    private String reason;
}