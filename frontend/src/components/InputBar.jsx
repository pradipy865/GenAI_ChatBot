import React, { useState, useRef, useCallback } from 'react';

const SUGGESTIONS = [
  "What are GitLab's core values?",
  "How does GitLab approach remote work?",
  "What is GitLab's product direction?",
  "How does engineering work at GitLab?",
  "What is the GitLab communication handbook?",
];

function InputBar({ onSend, isLoading }) {
  const [text, setText] = useState('');
  const textareaRef = useRef(null);

  const submit = useCallback(
    (value) => {
      const trimmed = value.trim();
      if (!trimmed || isLoading) return;
      onSend(trimmed);
      setText('');
      textareaRef.current?.focus();
    },
    [isLoading, onSend]
  );

  const handleFormSubmit = (e) => {
    e.preventDefault();
    submit(text);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      submit(text);
    }
  };

  // Auto-resize textarea up to max-height (handled by CSS)
  const handleChange = (e) => {
    setText(e.target.value);
    e.target.style.height = 'auto';
    e.target.style.height = Math.min(e.target.scrollHeight, 120) + 'px';
  };

  return (
    <div className="input-section">
      {/* Quick-start suggestion chips */}
      <div className="suggestions" role="list" aria-label="Suggested questions">
        {SUGGESTIONS.map((s, i) => (
          <button
            key={i}
            className="suggestion-chip"
            role="listitem"
            onClick={() => submit(s)}
            disabled={isLoading}
          >
            {s}
          </button>
        ))}
      </div>

      {/* Compose bar */}
      <form className="input-bar" onSubmit={handleFormSubmit}>
        <textarea
          ref={textareaRef}
          className="input-field"
          value={text}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          placeholder="Ask about GitLab's handbook, values, engineering..."
          disabled={isLoading}
          rows={1}
          aria-label="Message input"
        />
        <button
          type="submit"
          className="send-btn"
          disabled={!text.trim() || isLoading}
          aria-label="Send message"
        >
          {isLoading ? (
            <span className="spinner" />
          ) : (
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 2L11 13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          )}
        </button>
      </form>

      <p className="input-hint">Enter to send &nbsp;·&nbsp; Shift+Enter for new line</p>
    </div>
  );
}

export default InputBar;
