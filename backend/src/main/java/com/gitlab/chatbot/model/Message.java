package com.gitlab.chatbot.model;

/**
 * Represents a single turn in the conversation history.
 * role: "user" or "model" (Gemini convention)
 */
public record Message(String role, String content) {}
