package com.gitlab.chatbot.service;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GitLabDataService {

    private static final Logger log = LoggerFactory.getLogger(GitLabDataService.class);

    /**
     * Key GitLab Handbook and Direction pages to scrape at startup.
     * Add or remove URLs to adjust the knowledge base.
     */
    private static final List<String> HANDBOOK_URLS = List.of(
            "https://handbook.gitlab.com/handbook/values/",
            "https://handbook.gitlab.com/handbook/company/",
            "https://handbook.gitlab.com/handbook/engineering/",
            "https://handbook.gitlab.com/handbook/product/",
            "https://handbook.gitlab.com/handbook/people-group/",
            "https://handbook.gitlab.com/handbook/communication/",
            "https://about.gitlab.com/direction/"
    );

    // URL -> cleaned text content (truncated)
    private final Map<String, String> pageCache = new LinkedHashMap<>();
    private LocalDateTime lastDataRefresh;

    /**
     * Context search result with metadata for transparency.
     */
    public record ContextResult(
            String context,
            int relevanceScore,
            double confidence,
            List<String> usedSources
    ) {}

    @PostConstruct
    public void loadData() {
        log.info("Loading GitLab Handbook data ({} pages)...", HANDBOOK_URLS.size());
        int loaded = 0;
        for (String url : HANDBOOK_URLS) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (compatible; GitLabChatbot/1.0; +https://github.com)")
                        .timeout(12_000)
                        .get();

                // Try to extract main content area; fall back to body
                Element contentEl = doc.selectFirst("main, article, [class*='content'], [id*='content']");
                if (contentEl == null) contentEl = doc.body();

                String text = contentEl.text();
                // Limit per page to keep context manageable
                if (text.length() > 12000) text = text.substring(0, 12000);

                pageCache.put(url, text);
                loaded++;
                log.info("Loaded ({}/{}): {}", loaded, HANDBOOK_URLS.size(), url);

            } catch (IOException e) {
                log.warn("Could not load {}: {} — skipping.", url, e.getMessage());
            }
        }
        lastDataRefresh = LocalDateTime.now();
        log.info("GitLab data load complete. {}/{} pages cached.", loaded, HANDBOOK_URLS.size());
    }

    public LocalDateTime getLastDataRefresh() {
        return lastDataRefresh;
    }

    /**
     * Enhanced context search with confidence scoring and metadata.
     */
    public ContextResult findRelevantContextWithMetadata(String query) {
        if (pageCache.isEmpty()) {
            return new ContextResult("", 0, 0.0, List.of());
        }

        String lowerQuery = query.toLowerCase();
        String[] keywords = lowerQuery.split("\\s+");

        Map<String, Integer> scores = new HashMap<>();
        int maxPossibleScore = keywords.length * 10; // Rough estimate

        for (Map.Entry<String, String> entry : pageCache.entrySet()) {
            String lowerContent = entry.getValue().toLowerCase();
            int score = 0;
            for (String keyword : keywords) {
                if (keyword.length() > 2) {
                    int idx = 0;
                    while ((idx = lowerContent.indexOf(keyword, idx)) != -1) {
                        score++;
                        idx++;
                    }
                }
            }
            scores.put(entry.getKey(), score);
        }

        List<String> usedSources = new ArrayList<>();
        StringBuilder context = new StringBuilder();
        int totalScore = scores.values().stream()
                .filter(s -> s > 0)
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(Integer::intValue)
                .sum();

        scores.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> {
                    String url = e.getKey();
                    String content = pageCache.get(url);
                    int limit = Math.min(4000, content.length());
                    context.append("--- Source: ").append(url).append(" ---\n");
                    context.append(content, 0, limit).append("\n\n");
                    usedSources.add(url);
                });

        // Calculate confidence: 0.0 if no matches, up to 1.0 for strong matches
        double confidence = totalScore > 0 
                ? Math.min(1.0, (double) totalScore / maxPossibleScore)
                : 0.0;

        // Relevance score 0-100
        int relevanceScore = (int) (confidence * 100);

        return new ContextResult(context.toString(), relevanceScore, confidence, usedSources);
    }

    /**
     * Legacy method for backward compatibility - returns just the context string.
     */
    public String findRelevantContext(String query) {
        return findRelevantContextWithMetadata(query).context();
    }

    /**
     * Returns all successfully loaded source URLs.
     */
    public List<String> getLoadedSources() {
        return new ArrayList<>(pageCache.keySet());
    }
}

