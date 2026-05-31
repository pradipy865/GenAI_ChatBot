import React, { useState, useCallback, useEffect } from 'react';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';
import ChatWindow from './components/ChatWindow';
import InputBar from './components/InputBar';
import QueryTemplates from './components/QueryTemplates';
import './App.css';

const SESSION_STORAGE_KEY = 'gitlab_chat_session_id';
const DARK_MODE_KEY = 'gitlab_chat_dark_mode';

function getOrCreateSessionId() {
  let id = sessionStorage.getItem(SESSION_STORAGE_KEY);
  if (!id) {
    id = uuidv4();
    sessionStorage.setItem(SESSION_STORAGE_KEY, id);
  }
  return id;
}

const WELCOME_MESSAGE = {
  id: 'welcome',
  role: 'assistant',
  content:
    "👋 Hi! I'm the **GitLab Handbook Assistant**.\n\nAsk me anything about GitLab's values, engineering practices, product direction, or how things work at GitLab. I pull context directly from the official Handbook and Direction pages.\n\n💡 Try a quick-start template below or ask your own question!",
  timestamp: new Date(),
};

function App() {
  const [messages, setMessages] = useState([WELCOME_MESSAGE]);
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId] = useState(getOrCreateSessionId);
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem(DARK_MODE_KEY) === 'true';
  });
  const [showTemplates, setShowTemplates] = useState(true);
  const [showShortcutsHelp, setShowShortcutsHelp] = useState(false);

  // Apply dark mode
  useEffect(() => {
    document.body.classList.toggle('dark-mode', darkMode);
    localStorage.setItem(DARK_MODE_KEY, darkMode);
  }, [darkMode]);

  // Keyboard shortcuts
  useEffect(() => {
    const handleKeyPress = (e) => {
      // Cmd/Ctrl + K: Clear chat
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        clearChat();
      }
      // Cmd/Ctrl + /: Show shortcuts help
      if ((e.metaKey || e.ctrlKey) && e.key === '/') {
        e.preventDefault();
        setShowShortcutsHelp((prev) => !prev);
      }
      // Cmd/Ctrl + D: Toggle dark mode
      if ((e.metaKey || e.ctrlKey) && e.key === 'd') {
        e.preventDefault();
        setDarkMode((prev) => !prev);
      }
      // Cmd/Ctrl + E: Export conversation
      if ((e.metaKey || e.ctrlKey) && e.key === 'e') {
        e.preventDefault();
        exportConversation();
      }
    };

    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, [messages]);

  const sendMessage = useCallback(
    async (text) => {
      if (!text.trim() || isLoading) return;

      // Hide templates after first message
      setShowTemplates(false);

      const userMessage = {
        id: uuidv4(),
        role: 'user',
        content: text,
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, userMessage]);
      setIsLoading(true);

      try {
        const { data } = await axios.post('/api/chat', {
          message: text,
          sessionId,
        });

        const assistantMessage = {
          id: uuidv4(),
          role: 'assistant',
          content: data.response,
          sources: data.sources,
          metadata: data.metadata,
          warnings: data.warnings,
          suggestedFollowUps: data.suggestedFollowUps,
          timestamp: new Date(),
        };

        setMessages((prev) => [...prev, assistantMessage]);
      } catch (error) {
        const isRateLimit = error.response?.status === 429;
        const errorMessage = {
          id: uuidv4(),
          role: 'error',
          content: isRateLimit
            ? 'Rate limit reached. Please wait a moment and try again.'
            : 'Something went wrong connecting to the server. Please check that the backend is running and try again.',
          timestamp: new Date(),
        };
        setMessages((prev) => [...prev, errorMessage]);
      } finally {
        setIsLoading(false);
      }
    },
    [isLoading, sessionId]
  );

  const clearChat = useCallback(async () => {
    try {
      await axios.delete(`/api/session/${sessionId}`);
    } catch {
      // Best-effort clear
    }
    setMessages([
      {
        ...WELCOME_MESSAGE,
        id: uuidv4(),
        timestamp: new Date(),
      },
    ]);
    setShowTemplates(true);
  }, [sessionId]);

  const submitFeedback = useCallback(async (feedback) => {
    try {
      await axios.post('/api/feedback', feedback);
    } catch (error) {
      console.error('Failed to submit feedback:', error);
    }
  }, []);

  const exportConversation = useCallback(() => {
    const markdown = messages
      .map((msg) => {
        const role = msg.role === 'user' ? '**You**' : '**GitLab Assistant**';
        const time = msg.timestamp?.toLocaleString() || '';
        return `${role} (${time}):\n${msg.content}\n`;
      })
      .join('\n---\n\n');

    const blob = new Blob([markdown], { type: 'text/markdown' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `gitlab-chat-${new Date().toISOString().split('T')[0]}.md`;
    a.click();
    URL.revokeObjectURL(url);
  }, [messages]);

  const toggleDarkMode = () => setDarkMode((prev) => !prev);

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-brand">
          {/* GitLab logo SVG */}
          <svg
            className="gitlab-logo"
            viewBox="0 0 380 380"
            xmlns="http://www.w3.org/2000/svg"
            aria-label="GitLab"
          >
            <path
              d="M282.83 170.73l-.27-.69-26.14-68.22a6.81 6.81 0 00-2.58-3.03 7 7 0 00-8.8 1.94 6.99 6.99 0 00-1.22 3.59l-17.65 54h-71.22l-17.65-54a6.86 6.86 0 00-1.22-3.59 7 7 0 00-8.8-1.94 6.85 6.85 0 00-2.58 3.03L97.44 170l-.26.69a48.54 48.54 0 0016.1 56.1l.09.07.24.17 39.82 29.82 19.7 14.91 12 9.06a8.07 8.07 0 009.63 0l12-9.06 19.7-14.91 40.06-30 .1-.08a48.56 48.56 0 0016.1-56.04z"
              fill="#E24329"
            />
            <path
              d="M282.83 170.73l-.27-.69a88.3 88.3 0 00-35.15 15.8L190 229.25c19.55 14.79 36.57 27.64 36.57 27.64l40.06-30 .1-.08a48.56 48.56 0 0016.1-56.08z"
              fill="#FC6D26"
            />
            <path
              d="M153.43 256.89l19.7 14.91 12 9.06a8.07 8.07 0 009.63 0l12-9.06 19.7-14.91S209.55 244 190 229.25c-19.55 14.75-36.57 27.64-36.57 27.64z"
              fill="#FCA326"
            />
            <path
              d="M132.58 185.84A88.19 88.19 0 0097.44 170l-.26.69a48.54 48.54 0 0016.1 56.1l.09.07.24.17 39.82 29.82S170.45 244 190 229.25l-57.42-43.41z"
              fill="#FC6D26"
            />
          </svg>
          <div>
            <h1>GitLab Handbook Assistant</h1>
            <span className="header-subtitle">Powered by Gemini AI • Transparent & Secure</span>
          </div>
        </div>
        <div className="header-actions">
          <button
            className="icon-btn"
            onClick={toggleDarkMode}
            title={`Switch to ${darkMode ? 'light' : 'dark'} mode (⌘D)`}
            aria-label="Toggle dark mode"
          >
            {darkMode ? '☀️' : '🌙'}
          </button>
          <button
            className="icon-btn"
            onClick={exportConversation}
            title="Export conversation (⌘E)"
            aria-label="Export conversation"
          >
            📥
          </button>
          <button
            className="icon-btn"
            onClick={() => setShowShortcutsHelp((prev) => !prev)}
            title="Keyboard shortcuts (⌘/)"
            aria-label="Show keyboard shortcuts"
          >
            ⌨️
          </button>
          <button className="clear-btn" onClick={clearChat} title="Start a new conversation (⌘K)">
            New Chat
          </button>
        </div>
      </header>

      {/* Keyboard shortcuts help */}
      {showShortcutsHelp && (
        <div className="shortcuts-help">
          <h3>⌨️ Keyboard Shortcuts</h3>
          <ul>
            <li>
              <kbd>⌘K</kbd> or <kbd>Ctrl+K</kbd> — New chat
            </li>
            <li>
              <kbd>⌘D</kbd> or <kbd>Ctrl+D</kbd> — Toggle dark mode
            </li>
            <li>
              <kbd>⌘E</kbd> or <kbd>Ctrl+E</kbd> — Export conversation
            </li>
            <li>
              <kbd>⌘/</kbd> or <kbd>Ctrl+/</kbd> — Show/hide shortcuts
            </li>
          </ul>
        </div>
      )}

      <main className="chat-container">
        {showTemplates && <QueryTemplates onSelect={sendMessage} isLoading={isLoading} />}
        <ChatWindow
          messages={messages}
          isLoading={isLoading}
          onFollowUpSelect={sendMessage}
          onFeedbackSubmit={submitFeedback}
          sessionId={sessionId}
        />
        <InputBar onSend={sendMessage} isLoading={isLoading} />
      </main>
    </div>
  );
}

export default App;
