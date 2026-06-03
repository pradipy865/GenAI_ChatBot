package com.gitlab.chatbot.service;

import com.gitlab.chatbot.model.ChatResponse.GuardrailWarning;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Guardrail service for content validation, scope checking, and safety.
 */
@Service
public class GuardrailService {

    private static final List<String> GITLAB_KEYWORDS = Arrays.asList(
    "gitlab", "handbook", "values", "engineering", "product", "remote",
    "communication", "company", "policy", "pto", "vacation", "salary",
    "benefit", "onboarding", "career", "promotion", "interview",
    // ADD THESE:
    "leave", "time off", "work", "team", "manager", "employee",
    "process", "review", "meeting", "project", "deploy", "merge",
    "issue", "milestone", "sprint", "okr", "goal", "feedback"
);

    private static final List<String> SENSITIVE_PATTERNS = Arrays.asList(
            "password", "api key", "secret", "token", "credential",
            "ssn", "social security", "credit card", "bank account"
    );

    private static final List<String> VERIFICATION_NEEDED_TOPICS = Arrays.asList(
            "salary", "compensation", "bonus", "stock", "equity",
            "legal", "contract", "termination", "lawsuit"
    );

    /**
     * Check if query is GitLab-related (on-topic).
     */
    public boolean isOnTopic(String query) {
        String lowerQuery = query.toLowerCase();
        return GITLAB_KEYWORDS.stream()
                .anyMatch(lowerQuery::contains);
    }

    /**
     * Generate warnings based on query content and context.
     */
    public List<GuardrailWarning> generateWarnings(
            String query,
            LocalDateTime dataFreshness,
            double confidence,
            boolean hasHandbookContext
    ) {
        List<GuardrailWarning> warnings = new ArrayList<>();

        // Check for off-topic queries
        if (!isOnTopic(query)) {
            warnings.add(new GuardrailWarning(
                    "scope",
                    "This question may be outside my expertise. I'm specifically trained on GitLab's Handbook and Direction pages.",
                    "info"
            ));
        }

        // Check for sensitive information requests
        if (containsSensitivePattern(query)) {
            warnings.add(new GuardrailWarning(
                    "sensitive",
                    "⚠️ Never share passwords, API keys, or personal credentials in this chat. For sensitive information, contact your manager or People Ops directly.",
                    "critical"
            ));
        }

        // Check for topics requiring verification
        if (requiresVerification(query)) {
            warnings.add(new GuardrailWarning(
                    "verification-needed",
                    "💼 For official information about compensation, legal matters, or contracts, please verify with your manager or People Ops. This is AI-generated guidance only.",
                    "warning"
            ));
        }

        // Check data freshness
        if (dataFreshness != null) {
            long daysSinceRefresh = ChronoUnit.DAYS.between(dataFreshness, LocalDateTime.now());
            if (daysSinceRefresh > 7) {
                warnings.add(new GuardrailWarning(
                        "outdated",
                        String.format("📅 Handbook data was last refreshed %d days ago. For the most current information, check handbook.gitlab.com directly.", daysSinceRefresh),
                        "info"
                ));
            }
        }

        // Low confidence warning
        if (confidence < 0.3 && hasHandbookContext) {
            warnings.add(new GuardrailWarning(
                    "low-confidence",
                    "🔍 I found limited information in the handbook for this query. Consider refining your question or checking the source directly.",
                    "info"
            ));
        }

        return warnings;
    }

    /**
     * Generate suggested follow-up questions based on the query.
     */
    public List<String> generateSuggestedFollowUps(String query, List<String> sources) {
        List<String> suggestions = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("value") && !lowerQuery.contains("credit")) {
            suggestions.add("What does CREDIT stand for in GitLab values?");
            suggestions.add("How are GitLab values applied in day-to-day work?");
        } else if (lowerQuery.contains("remote")) {
            suggestions.add("What tools does GitLab use for remote collaboration?");
            suggestions.add("How does GitLab handle timezone differences?");
        } else if (lowerQuery.contains("engineering") || lowerQuery.contains("development")) {
            suggestions.add("What is GitLab's code review process?");
            suggestions.add("How does GitLab approach testing and CI/CD?");
        } else if (lowerQuery.contains("onboarding") || lowerQuery.contains("new")) {
            suggestions.add("What's in the first 30 days for new GitLab employees?");
            suggestions.add("How do I access GitLab's internal systems?");
        } else if (lowerQuery.contains("communication")) {
            suggestions.add("What are GitLab's guidelines for async communication?");
            suggestions.add("When should I use Slack vs issues vs email?");
        } else if (lowerQuery.contains("product")) {
            suggestions.add("What is GitLab's product vision for the next year?");
            suggestions.add("How does GitLab prioritize features?");
        }

        // Add source-specific suggestions
        if (sources.stream().anyMatch(s -> s.contains("/values/"))) {
            suggestions.add("How can I demonstrate GitLab values in my role?");
        }
        if (sources.stream().anyMatch(s -> s.contains("/engineering/"))) {
            suggestions.add("What are GitLab's engineering career paths?");
        }

        return suggestions.stream().distinct().limit(3).toList();
    }

    private boolean containsSensitivePattern(String query) {
        String lowerQuery = query.toLowerCase();
        return SENSITIVE_PATTERNS.stream().anyMatch(lowerQuery::contains);
    }

    private boolean requiresVerification(String query) {
        String lowerQuery = query.toLowerCase();
        return VERIFICATION_NEEDED_TOPICS.stream().anyMatch(lowerQuery::contains);
    }
}
