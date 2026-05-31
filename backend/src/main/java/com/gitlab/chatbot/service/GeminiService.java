package com.gitlab.chatbot.service;

import com.gitlab.chatbot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private static final String GEMINI_API_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    /**
     * Calls the Gemini API with a system prompt, conversation history, and the current user message.
     * Supports multi-turn conversations via the contents array.
     */
    public String generateResponse(String systemPrompt, List<Message> history, String userMessage) {
        List<Map<String, Object>> contents = new ArrayList<>();

        // Append prior turns (already stored as user/model pairs)
        for (Message msg : history) {
            contents.add(Map.of(
                    "role", msg.role(),
                    "parts", List.of(Map.of("text", msg.content()))
            ));
        }

        // Add the current user message
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))
                ),
                "contents", contents,
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 1024,
                        "topP", 0.9
                )
        );

        try {
            Map<?, ?> response = webClient.post()
                    .uri(GEMINI_API_BASE + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.TOO_MANY_REQUESTS,
                            r -> r.bodyToMono(String.class).map(
                                    body -> new RuntimeException("Rate limit reached. Please wait a moment and try again.")))
                    .onStatus(HttpStatusCode::is4xxClientError,
                            r -> r.bodyToMono(String.class).map(
                                    body -> new RuntimeException("Gemini API client error: " + body)))
                    .onStatus(HttpStatusCode::is5xxServerError,
                            r -> r.bodyToMono(String.class).map(
                                    body -> new RuntimeException("Gemini API server error. Please try again.")))
                    .bodyToMono(Map.class)
                    .block();

            return extractText(response);

        } catch (WebClientResponseException e) {
            log.error("Gemini API HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "I encountered an error communicating with the AI service. Please try again.";
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
            return e.getMessage().contains("Rate limit")
                    ? e.getMessage()
                    : "I'm unable to respond right now. Please try again in a moment.";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> response) {
        if (response == null) return "No response received from AI service.";
        try {
            var candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "No response generated.";
            var content = (Map<String, Object>) candidates.get(0).get("content");
            var parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            return "Error parsing AI response. Please try again.";
        }
    }
}
