package com.gitlab.chatbot.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Exportable conversation for sharing or archiving.
 */
public record ConversationExport(
        String sessionId,
        LocalDateTime exportedAt,
        List<Message> messages,
        int messageCount,
        String format           // "json", "markdown", "pdf"
) {}
