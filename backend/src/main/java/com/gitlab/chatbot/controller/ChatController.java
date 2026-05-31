package com.gitlab.chatbot.controller;

import com.gitlab.chatbot.model.*;
import com.gitlab.chatbot.service.ChatService;
import com.gitlab.chatbot.service.FeedbackService;
import com.gitlab.chatbot.service.GitLabDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final GitLabDataService gitLabDataService;
    private final FeedbackService feedbackService;

    public ChatController(
            ChatService chatService,
            GitLabDataService gitLabDataService,
            FeedbackService feedbackService
    ) {
        this.chatService = chatService;
        this.gitLabDataService = gitLabDataService;
        this.feedbackService = feedbackService;
    }

    /**
     * Main chat endpoint. Accepts a user message + sessionId for conversation continuity.
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }
        ChatResponse response = chatService.processChat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns loaded GitLab Handbook source URLs.
     */
    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources() {
        return ResponseEntity.ok(gitLabDataService.getLoadedSources());
    }

    /**
     * Clears the conversation history for a given session.
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "sourcesLoaded", gitLabDataService.getLoadedSources().size(),
                "lastDataRefresh", gitLabDataService.getLastDataRefresh() != null 
                        ? gitLabDataService.getLastDataRefresh().toString() 
                        : "unknown"
        ));
    }

    /**
     * Get query templates for quick start.
     */
    @GetMapping("/templates")
    public ResponseEntity<List<QueryTemplate>> getTemplates() {
        return ResponseEntity.ok(Arrays.asList(QueryTemplate.COMMON_TEMPLATES));
    }

    /**
     * Submit user feedback on a response.
     */
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> submitFeedback(@RequestBody FeedbackRequest feedback) {
        feedbackService.recordFeedback(feedback);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Thank you for your feedback!"));
    }

    /**
     * Get feedback statistics (admin/analytics endpoint).
     */
    @GetMapping("/feedback/stats")
    public ResponseEntity<Map<String, Object>> getFeedbackStats() {
        return ResponseEntity.ok(feedbackService.getStatistics());
    }
}
