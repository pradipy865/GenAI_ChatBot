import React from 'react';
import ReactMarkdown from 'react-markdown';
import TransparencyPanel from './TransparencyPanel';
import SuggestedFollowUps from './SuggestedFollowUps';
import FeedbackWidget from './FeedbackWidget';

function MessageBubble({ message, onFollowUpSelect, onFeedbackSubmit, sessionId, isLoading }) {
  const isUser = message.role === 'user';
  const isError = message.role === 'error';

  const bubbleClass = isUser ? 'user-bubble' : isError ? 'error-bubble' : 'assistant-bubble';

  return (
    <div className={`message-row ${isUser ? 'user' : isError ? 'error' : 'assistant'}`}>
      {/* Avatar — left side for assistant/error, right side for user */}
      {!isUser && (
        <div className={`avatar ${isError ? 'error-avatar' : 'assistant-avatar'}`}>
          {isError ? '!' : 'GL'}
        </div>
      )}

      <div className={`message-bubble ${bubbleClass}`}>
        {isUser ? <p>{message.content}</p> : <ReactMarkdown>{message.content}</ReactMarkdown>}

        {/* Transparency metadata and warnings */}
        {!isUser && !isError && (message.metadata || message.warnings) && (
          <TransparencyPanel metadata={message.metadata} warnings={message.warnings} />
        )}

        {/* Source links — shown only on assistant messages with sources */}
        {!isUser && !isError && message.sources && message.sources.length > 0 && (
          <div className="sources">
            <p className="sources-label">📚 Sources</p>
            <ul>
              {message.sources.slice(0, 4).map((src, i) => (
                <li key={i}>
                  <a href={src} target="_blank" rel="noopener noreferrer">
                    {src.replace(/^https?:\/\//, '').replace(/\/$/, '')}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Suggested follow-ups */}
        {!isUser && !isError && message.suggestedFollowUps && (
          <SuggestedFollowUps
            suggestions={message.suggestedFollowUps}
            onSelect={onFollowUpSelect}
            isLoading={isLoading}
          />
        )}

        {/* Feedback widget */}
        {!isUser && !isError && message.id && (
          <FeedbackWidget
            messageId={message.id}
            sessionId={sessionId}
            onSubmit={onFeedbackSubmit}
          />
        )}

        <span className="message-time">
          {message.timestamp?.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </span>
      </div>

      {isUser && <div className="avatar user-avatar">You</div>}
    </div>
  );
}

export default MessageBubble;
