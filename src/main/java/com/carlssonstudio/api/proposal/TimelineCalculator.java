package com.carlssonstudio.api.proposal;

import com.carlssonstudio.api.entity.ProposalComplexity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TimelineCalculator {

    public int calculateWeeks(List<String> features,
                              String companySize) {
        int base = 4;
        int featureScore = features == null ? 0 : features.size();

        // Add weeks based on feature count
        if (featureScore > 8)       base += 8;
        else if (featureScore > 5)  base += 4;
        else if (featureScore > 3)  base += 2;

        // Add weeks for AI features
        if (features != null && features.contains("AI")) base += 3;
        if (features != null && features.contains("Mobile")) base += 2;
        if (features != null && features.contains("API")) base += 1;

        // Adjust for company size complexity
        if ("100+".equals(companySize))   base += 4;
        else if ("20-100".equals(companySize)) base += 2;

        return base;
    }

    public ProposalComplexity calculateComplexity(
            List<String> features, String companySize) {
        int score = 0;
        if (features != null) score += features.size();
        if (features != null && features.contains("AI")) score += 3;
        if (features != null && features.contains("Mobile")) score += 2;
        if ("100+".equals(companySize)) score += 3;
        else if ("20-100".equals(companySize)) score += 1;

        if (score > 10)     return ProposalComplexity.HIGH;
        else if (score > 6) return ProposalComplexity.MEDIUM;
        else                return ProposalComplexity.LOW;
    }
}