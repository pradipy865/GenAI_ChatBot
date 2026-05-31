package com.gitlab.chatbot.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Enhanced chat response with transparency and guardrail metadata.
 */
public record ChatResponse(
        String response,
        List<String> sources,
        String sessionId,
        TransparencyMetadata metadata,
        List<String> suggestedFollowUps,
        List<GuardrailWarning> warnings
) {
    public record TransparencyMetadata(
            double confidenceScore,      // 0.0-1.0: how confident we are in the response
            String dataSource,            // "handbook", "ai-knowledge", "hybrid"
            LocalDateTime dataFreshness,  // when handbook data was last updated
            int relevanceScore,           // 0-100: how relevant the found context is
            boolean isOnTopic             // whether query is GitLab-related
    ) {}

    public record GuardrailWarning(
            String type,                  // "outdated", "scope", "verification-needed", "sensitive"
            String message,
            String severity               // "info", "warning", "critical"
    ) {}
}
