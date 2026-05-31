package com.gitlab.chatbot.model;

/**
 * Pre-defined query templates for common employee questions.
 */
public record QueryTemplate(
        String id,
        String category,        // "values", "engineering", "benefits", "onboarding", etc.
        String title,           // Display title
        String query,           // The actual query text to send
        String icon,            // Emoji icon for UI
        String description      // Helper text
) {
    public static final QueryTemplate[] COMMON_TEMPLATES = {
            new QueryTemplate("values", "culture", "GitLab Values", 
                    "What are GitLab's core values and what do they mean?", 
                    "💎", "Learn about our CREDIT values"),
            
            new QueryTemplate("remote", "culture", "Remote Work", 
                    "How does GitLab approach remote work and what are the best practices?", 
                    "🌍", "All-remote work guidelines"),
            
            new QueryTemplate("engineering", "technical", "Engineering Practices", 
                    "What are GitLab's engineering best practices and development workflow?", 
                    "⚙️", "Development workflow and standards"),
            
            new QueryTemplate("communication", "culture", "Communication Guidelines", 
                    "What are GitLab's communication guidelines and best practices?", 
                    "💬", "How we communicate effectively"),
            
            new QueryTemplate("product", "business", "Product Direction", 
                    "What is GitLab's product strategy and roadmap?", 
                    "🎯", "Product vision and direction"),
            
            new QueryTemplate("onboarding", "hr", "New Employee Onboarding", 
                    "What should I know as a new GitLab employee during onboarding?", 
                    "🚀", "Getting started at GitLab"),
            
            new QueryTemplate("pto", "hr", "Time Off Policy", 
                    "What is GitLab's paid time off and vacation policy?", 
                    "🏖️", "PTO and vacation guidelines"),
            
            new QueryTemplate("contribute", "technical", "Contributing to GitLab", 
                    "How can I contribute to GitLab's open source projects?", 
                    "🤝", "Contribution guidelines")
    };
}
