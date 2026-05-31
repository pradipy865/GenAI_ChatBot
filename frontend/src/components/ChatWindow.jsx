import React, { useEffect, useRef } from 'react';
import MessageBubble from './MessageBubble';

function TypingIndicator() {
  return (
    <div className="message-row assistant">
      <div className="avatar assistant-avatar">GL</div>
      <div className="typing-indicator" aria-label="Assistant is typing">
        <span />
        <span />
        <span />
      </div>
    </div>
  );
}

function ChatWindow({ messages, isLoading, onFollowUpSelect, onFeedbackSubmit, sessionId }) {
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  return (
    <div className="chat-window" role="log" aria-live="polite" aria-label="Conversation">
      {messages.map((msg) => (
        <MessageBubble
          key={msg.id}
          message={msg}
          onFollowUpSelect={onFollowUpSelect}
          onFeedbackSubmit={onFeedbackSubmit}
          sessionId={sessionId}
          isLoading={isLoading}
        />
      ))}
      {isLoading && <TypingIndicator />}
      <div ref={bottomRef} />
    </div>
  );
}

export default ChatWindow;
