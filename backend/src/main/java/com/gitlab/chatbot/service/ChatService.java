package com.gitlab.chatbot.service;

import com.gitlab.chatbot.model.ChatRequest;
import com.gitlab.chatbot.model.ChatResponse;
import com.gitlab.chatbot.model.Message;
import com.gitlab.chatbot.service.GitLabDataService.ContextResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            You are a knowledgeable assistant for GitLab employees and aspiring employees.
            You have access to content scraped from GitLab's Handbook and Direction pages.

            Guidelines:
            - Answer questions using the provided context from GitLab's resources when available.
            - If the context doesn't fully cover the question, supplement with your general knowledge about GitLab.
            - Be concise, accurate, and helpful. Use markdown formatting (bullet points, headers, code blocks) where appropriate.
            - When referencing specific handbook sections, mention the source URL.
            - If you genuinely don't know something, say so clearly rather than guessing.
            - Keep answers focused and practical for someone working at or joining GitLab.
            - Always remind users to verify critical information (compensation, legal, contracts) with official sources.
            """;

    // Maximum number of message turns to keep per session (each exchange = 2 messages)
    private static final int MAX_HISTORY_SIZE = 20;

    private final GeminiService geminiService;
    private final GitLabDataService gitLabDataService;
    private final GuardrailService guardrailService;

    // In-memory session store: sessionId -> conversation history
    private final Map<String, List<Message>> sessions = new ConcurrentHashMap<>();

    public ChatService(
            GeminiService geminiService,
            GitLabDataService gitLabDataService,
            GuardrailService guardrailService
    ) {
        this.geminiService = geminiService;
        this.gitLabDataService = gitLabDataService;
        this.guardrailService = guardrailService;
    }

    public ChatResponse processChat(ChatRequest request) {
        String sessionId = (request.sessionId() != null && !request.sessionId().isBlank())
                ? request.sessionId()
                : UUID.randomUUID().toString();

        String userMessage = request.message().trim();

        // Retrieve or create conversation history for this session
        List<Message> history = sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // Find relevant context with metadata
        ContextResult contextResult = gitLabDataService.findRelevantContextWithMetadata(userMessage);

        // Check if query is on-topic
        boolean isOnTopic = guardrailService.isOnTopic(userMessage);

        // Enrich the user message with context (but store the original in history)
        String enrichedMessage = contextResult.context().isBlank()
                ? userMessage
                : "Context from GitLab Handbook/Direction:\n\n" + contextResult.context() + "\n\nUser question: " + userMessage;

        // Call Gemini with full conversation history + enriched prompt
        String aiResponse = geminiService.generateResponse(SYSTEM_PROMPT, history, enrichedMessage);

        // Persist the original (not enriched) message and the AI response to history
        history.add(new Message("user", userMessage));
        history.add(new Message("model", aiResponse));

        // Trim history to prevent unbounded growth
        if (history.size() > MAX_HISTORY_SIZE) {
            history.subList(0, 2).clear();
        }

        // Determine data source type
        String dataSource = contextResult.confidence() > 0.5 ? "handbook" : 
                           contextResult.confidence() > 0.1 ? "hybrid" : 
                           "ai-knowledge";

        // Calculate overall confidence
        double overallConfidence = contextResult.confidence() * (isOnTopic ? 1.0 : 0.5);

        // Build transparency metadata
        ChatResponse.TransparencyMetadata metadata = new ChatResponse.TransparencyMetadata(
                overallConfidence,
                dataSource,
                gitLabDataService.getLastDataRefresh(),
                contextResult.relevanceScore(),
                isOnTopic
        );

        // Generate guardrail warnings
        List<ChatResponse.GuardrailWarning> warnings = guardrailService.generateWarnings(
                userMessage,
                gitLabDataService.getLastDataRefresh(),
                contextResult.confidence(),
                !contextResult.context().isBlank()
        );

        // Generate suggested follow-up questions
        List<String> suggestions = guardrailService.generateSuggestedFollowUps(
                userMessage,
                contextResult.usedSources()
        );

        return new ChatResponse(
                aiResponse,
                contextResult.usedSources(),
                sessionId,
                metadata,
                suggestions,
                warnings
        );
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
