import React from 'react';

/**
 * Query template cards for quick-start queries.
 */
function QueryTemplates({ onSelect, isLoading }) {
  const templates = [
    {
      id: 'values',
      category: 'culture',
      title: 'GitLab Values',
      query: "What are GitLab's core values and what do they mean?",
      icon: '💎',
      description: 'Learn about our CREDIT values',
    },
    {
      id: 'remote',
      category: 'culture',
      title: 'Remote Work',
      query: 'How does GitLab approach remote work and what are the best practices?',
      icon: '🌍',
      description: 'All-remote work guidelines',
    },
    {
      id: 'engineering',
      category: 'technical',
      title: 'Engineering Practices',
      query: "What are GitLab's engineering best practices and development workflow?",
      icon: '⚙️',
      description: 'Development workflow and standards',
    },
    {
      id: 'communication',
      category: 'culture',
      title: 'Communication Guidelines',
      query: "What are GitLab's communication guidelines and best practices?",
      icon: '💬',
      description: 'How we communicate effectively',
    },
    {
      id: 'product',
      category: 'business',
      title: 'Product Direction',
      query: "What is GitLab's product strategy and roadmap?",
      icon: '🎯',
      description: 'Product vision and direction',
    },
    {
      id: 'onboarding',
      category: 'hr',
      title: 'New Employee Onboarding',
      query: 'What should I know as a new GitLab employee during onboarding?',
      icon: '🚀',
      description: 'Getting started at GitLab',
    },
  ];

  return (
    <div className="query-templates">
      <h3 className="templates-title">Quick Start</h3>
      <div className="templates-grid">
        {templates.map((template) => (
          <button
            key={template.id}
            className="template-card"
            onClick={() => onSelect(template.query)}
            disabled={isLoading}
            aria-label={template.title}
          >
            <span className="template-icon">{template.icon}</span>
            <div className="template-content">
              <h4>{template.title}</h4>
              <p>{template.description}</p>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}

export default QueryTemplates;
