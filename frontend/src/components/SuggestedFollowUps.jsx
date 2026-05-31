import React from 'react';

/**
 * Suggested follow-up questions to continue the conversation.
 */
function SuggestedFollowUps({ suggestions, onSelect, isLoading }) {
  if (!suggestions || suggestions.length === 0) {
    return null;
  }

  return (
    <div className="suggested-followups">
      <p className="followups-label">💡 You might also want to ask:</p>
      <div className="followups-list">
        {suggestions.map((suggestion, index) => (
          <button
            key={index}
            className="followup-button"
            onClick={() => onSelect(suggestion)}
            disabled={isLoading}
          >
            {suggestion}
          </button>
        ))}
      </div>
    </div>
  );
}

export default SuggestedFollowUps;
