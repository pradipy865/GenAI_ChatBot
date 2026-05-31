package com.gitlab.chatbot.model;

/**
 * Incoming chat request from the frontend.
 * sessionId is used to maintain conversation history across multiple turns.
 */
public record ChatRequest(String message, String sessionId) {}
