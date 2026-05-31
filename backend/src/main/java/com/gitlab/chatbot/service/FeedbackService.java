package com.gitlab.chatbot.service;

import com.gitlab.chatbot.model.FeedbackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects and analyzes user feedback for continuous improvement.
 */
@Service
public class FeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);

    // In-memory storage for feedback (in production, use a database)
    private final List<FeedbackEntry> feedbackHistory = new ArrayList<>();
    private final Map<String, Integer> helpfulCount = new ConcurrentHashMap<>();
    private final Map<String, Integer> notHelpfulCount = new ConcurrentHashMap<>();

    public record FeedbackEntry(
            String sessionId,
            String messageId,
            String rating,
            String comment,
            String category,
            LocalDateTime timestamp
    ) {}

    /**
     * Record user feedback.
     */
    public void recordFeedback(FeedbackRequest feedback) {
        FeedbackEntry entry = new FeedbackEntry(
                feedback.sessionId(),
                feedback.messageId(),
                feedback.rating(),
                feedback.comment(),
                feedback.category(),
                LocalDateTime.now()
        );

        feedbackHistory.add(entry);

        // Update counters
        String key = feedback.category() != null ? feedback.category() : "general";
        if ("helpful".equals(feedback.rating())) {
            helpfulCount.merge(key, 1, Integer::sum);
        } else if ("not-helpful".equals(feedback.rating())) {
            notHelpfulCount.merge(key, 1, Integer::sum);
        }

        log.info("Feedback recorded: {} - {} (category: {})", 
                feedback.rating(), 
                feedback.messageId(), 
                key);
    }

    /**
     * Get aggregated feedback statistics.
     */
    public Map<String, Object> getStatistics() {
        int totalFeedback = feedbackHistory.size();
        long helpfulTotal = helpfulCount.values().stream().mapToInt(Integer::intValue).sum();
        long notHelpfulTotal = notHelpfulCount.values().stream().mapToInt(Integer::intValue).sum();

        double satisfactionRate = totalFeedback > 0 
                ? (double) helpfulTotal / (helpfulTotal + notHelpfulTotal) * 100 
                : 0.0;

        return Map.of(
                "totalFeedback", totalFeedback,
                "helpfulCount", helpfulTotal,
                "notHelpfulCount", notHelpfulTotal,
                "satisfactionRate", String.format("%.1f%%", satisfactionRate),
                "categoryBreakdown", Map.of(
                        "helpful", helpfulCount,
                        "notHelpful", notHelpfulCount
                )
        );
    }
}
