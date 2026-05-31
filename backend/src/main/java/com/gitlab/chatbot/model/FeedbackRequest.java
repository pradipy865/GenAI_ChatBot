package com.gitlab.chatbot.model;

/**
 * User feedback on chatbot responses.
 */
public record FeedbackRequest(
        String sessionId,
        String messageId,       // Which response is being rated
        String rating,          // "helpful", "not-helpful", "incorrect"
        String comment,         // Optional detailed feedback
        String category         // "accuracy", "relevance", "clarity", "other"
) {}
