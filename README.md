# 🚀 GitLab Handbook Assistant

An intelligent, transparent, and user-centric chatbot that helps GitLab employees and aspiring employees access information from GitLab's Handbook and Direction pages. Built with advanced guardrails, transparency features, and exceptional UX.

## ✨ Core Features

### 🎯 Basic Functionality
- **Interactive Chat Interface**: Real-time conversation with AI assistant
- **GitLab Handbook Integration**: Automatically scrapes and indexes key handbook pages
- **Multi-turn Conversations**: Maintains context across the session
- **Source Attribution**: Every response includes links to source documents

### 🛡️ Advanced Guardrails & Safety

#### Content Validation
- **Scope Detection**: Identifies when queries are outside GitLab topics
- **Sensitive Information Protection**: Warns users against sharing credentials or personal data
- **Verification Prompts**: Flags topics requiring official confirmation (compensation, legal, etc.)
- **Data Freshness Indicators**: Shows when handbook data was last updated

#### Transparency Features
- **Confidence Scoring**: 0-100 scale showing AI confidence in responses
- **Source Type Badges**: 
  - 📚 **Handbook Data**: Answer based on official documentation
  - 🤖 **AI Knowledge**: Using general GitLab knowledge
  - 🔄 **Mixed Sources**: Hybrid approach
- **Relevance Metrics**: Percentage showing how relevant found content is to query
- **On-Topic Indicators**: Flags whether query relates to GitLab

### 💡 Enhanced User Experience

#### Quick-Start Templates
Pre-built queries for common employee questions:
- 💎 GitLab Values & CREDIT principles
- 🌍 Remote work best practices
- ⚙️ Engineering workflows
- 💬 Communication guidelines
- 🎯 Product direction & roadmap
- 🚀 Onboarding information

#### Suggested Follow-Ups
Context-aware question recommendations based on:
- Current topic area
- Source documents used
- Common related queries

#### Dark Mode 🌙
- Automatic theme persistence
- Comfortable viewing in any environment
- Keyboard shortcut: `⌘D` / `Ctrl+D`

#### Keyboard Shortcuts ⌨️
Power user features for efficiency:
- `⌘K` / `Ctrl+K` — New chat
- `⌘D` / `Ctrl+D` — Toggle dark mode
- `⌘E` / `Ctrl+E` — Export conversation
- `⌘/` / `Ctrl+/` — Show/hide shortcuts help

#### Conversation Export 📥
- Download conversations as Markdown
- Includes timestamps and full message history
- Perfect for sharing or archiving important discussions

#### Feedback System 👍👎
- Rate individual responses (helpful/not helpful)
- Categorize feedback (accuracy, relevance, clarity)
- Add detailed comments for improvement
- Anonymous analytics for quality monitoring

## 🏗️ Architecture

### Backend (Spring Boot + Java 17)

#### Services
1. **ChatService**
   - Orchestrates conversation flow
   - Manages session state
   - Enriches queries with handbook context

2. **GeminiService**
   - Integrates Google Gemini AI API
   - Handles multi-turn conversations
   - Error handling and rate limiting

3. **GitLabDataService**
   - Web scraping with JSoup
   - Content caching and indexing
   - Confidence scoring algorithm
   - Relevance detection

4. **GuardrailService**
   - Query scope validation
   - Sensitive content detection
   - Warning generation
   - Follow-up suggestion engine

5. **FeedbackService**
   - Collects user ratings
   - Aggregates satisfaction metrics
   - Category-based analytics

#### Models
- `ChatRequest/ChatResponse`: API contracts with transparency metadata
- `QueryTemplate`: Pre-defined quick-start queries
- `FeedbackRequest`: User feedback structure
- `TransparencyMetadata`: Confidence, source type, freshness data
- `GuardrailWarning`: Safety warnings and notifications

### Frontend (React + Modern UX)

#### Components
- `ChatWindow`: Message display with auto-scroll
- `MessageBubble`: Individual message with metadata
- `QueryTemplates`: Quick-start template grid
- `TransparencyPanel`: Metadata badges and warnings
- `SuggestedFollowUps`: Context-aware question suggestions
- `FeedbackWidget`: Rating and feedback collection
- `InputBar`: Message input with keyboard support

#### Features
- Session persistence (localStorage + sessionStorage)
- Dark mode with CSS variables
- Keyboard navigation
- Markdown rendering with syntax highlighting
- Responsive design

## 🚦 Getting Started

### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+
- Gemini API Key

### Backend Setup

```bash
cd backend

# Set your Gemini API key
export GEMINI_API_KEY=your_api_key_here

# Build and run
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The frontend will start on `http://localhost:3000`

## 📊 API Endpoints

### Chat Endpoints
- `POST /api/chat` - Send message and receive AI response
- `DELETE /api/session/{sessionId}` - Clear conversation history
- `GET /api/sources` - List loaded handbook sources
- `GET /api/health` - Health check with data freshness

### Template & Feedback
- `GET /api/templates` - Get quick-start query templates
- `POST /api/feedback` - Submit feedback on responses
- `GET /api/feedback/stats` - View aggregated feedback statistics

## 🎨 Design Philosophy

### Transparency First
Every AI response includes:
- Clear indication of information source
- Confidence level
- Relevant warnings
- Data freshness timestamps

### Safety by Default
- Proactive warnings for sensitive topics
- Scope boundaries (GitLab-focused)
- Verification reminders for critical information
- No storage of user credentials

### Employee-Centric UX
- Quick access to common queries
- Keyboard shortcuts for power users
- Dark mode for different work environments
- Exportable conversations for reference
- Context-aware suggestions

## 🔒 Security & Privacy

### Data Handling
- **No user tracking**: Anonymous session IDs
- **No credential storage**: Warnings against sharing sensitive data
- **Local-first**: Session data in browser storage
- **Transparent AI**: Clear indication when AI is used

### Content Safety
- Input validation and sanitization
- Rate limiting on API calls
- Error boundaries and graceful degradation
- CORS configuration for secure access

## 📈 Future Enhancements

### Planned Features
- [ ] Semantic search (vs keyword matching)
- [ ] Broader handbook coverage (1000+ pages)
- [ ] Multi-language support
- [ ] Integration with Slack/Teams
- [ ] Bookmark important conversations
- [ ] Advanced analytics dashboard
- [ ] A/B testing for UX improvements

### Community Contributions
This project embodies GitLab's "build in public" philosophy. Contributions welcome!

## 📝 Technology Stack

**Backend:**
- Spring Boot 3.x
- Java 17
- Google Gemini AI API
- JSoup (web scraping)
- WebClient (reactive HTTP)

**Frontend:**
- React 18
- Axios (HTTP client)
- React Markdown
- UUID (session management)
- Modern CSS with variables

## 🤝 Contributing

Following GitLab's transparent approach:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request
5. Engage in code review

## 📄 License

MIT License - feel free to use, modify, and distribute.

## 🙏 Acknowledgments

Inspired by GitLab's commitment to transparency, remote work, and building in public. This project demonstrates how AI can be made transparent, safe, and user-friendly for enterprise use.

---

**Built with transparency, secured with guardrails, designed for employees** 🚀
