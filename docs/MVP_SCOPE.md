# 🎯 MVP Scope - Phase 1

> Defining the Minimum Viable Product for J.A.R.V.I.S. to avoid scope creep and ensure focused development.

---

## 🏁 MVP Goal

**Build a working on-device AI chat assistant that learns from conversations and improves over time.**

By the end of Phase 1, we should have:
- ✅ A chat interface that works
- ✅ LLM running 100% on-device
- ✅ Basic learning from corrections
- ✅ Proof that accuracy improves over time

**NOT in MVP:**
- ❌ Voice input/output
- ❌ Avatar system
- ❌ Device control/automation
- ❌ AccessibilityService
- ❌ Shizuku integration
- ❌ Widgets

---

## 📦 MVP Features

### ✅ Must Have (P0)

| Feature | Description | Acceptance Criteria |
|---------|-------------|---------------------|
| **Chat UI** | Basic chat interface | Send message, see streaming response |
| **LLM Inference** | Phi-3.5-mini running on-device | <5 sec first token, ~10 tok/sec |
| **Conversation History** | Persist chats | Survives app restart |
| **Correction Learning** | Store user corrections | User can correct response, stored in DB |
| **Context Injection** | Use learnings in prompts | Corrections appear in system prompt |

### 🟡 Should Have (P1)

| Feature | Description | Acceptance Criteria |
|---------|-------------|---------------------|
| **Preference Extraction** | Extract preferences from chats | Auto-detect "I prefer X" statements |
| **Pattern Detection** | Recognize repeated questions | Log patterns, show in debug |
| **Accuracy Metrics** | Track correction rate | Show stats: total chats, corrections, rate |
| **Model Loading UI** | Progress while loading model | Show loading indicator with progress |

### 🟢 Nice to Have (P2)

| Feature | Description | Acceptance Criteria |
|---------|-------------|---------------------|
| **Multiple Conversations** | Separate chat threads | List of conversations, create new |
| **Dark Mode** | Theme support | Toggle dark/light |
| **Export Data** | Export learnings as JSON | Button to export all data |
| **Settings Screen** | Basic settings | Model path, clear data |

### ❌ Not in MVP (Defer to Phase 2+)

| Feature | Reason to Defer |
|---------|-----------------|
| Voice Input (STT) | Adds complexity, focus on core learning |
| Voice Output (TTS) | Adds complexity |
| Avatar | Significant UI work, not core value |
| Device Control | Requires AccessibilityService setup |
| Shizuku Integration | Complex, security considerations |
| Notifications | Requires background service |
| Widgets | Requires app widgets setup |
| RAG/Embeddings | Can work without initially |
| Wake Word | Requires always-on processing |

---

## 🏗️ MVP Architecture

### Modules Needed for MVP

```
jarvis-ai/
├── app/                    # ✅ MVP - Main app
├── core/
│   ├── core-common/        # ✅ MVP - Utilities
│   └── core-model/         # ✅ MVP - Domain models
├── domain/                 # ✅ MVP - UseCases
├── data/                   # ✅ MVP - Room, DataStore
├── ai/
│   ├── ai-core/            # ✅ MVP - Interfaces
│   └── ai-llama/           # ✅ MVP - llama.cpp
├── learning/               # ✅ MVP - Basic learning
└── feature/
    └── feature-chat/       # ✅ MVP - Chat screen
```

### Modules NOT Needed for MVP

```
├── ai/
│   ├── ai-whisper/         # ❌ Phase 2
│   ├── ai-piper/           # ❌ Phase 2
│   ├── ai-embeddings/      # ❌ Phase 2
│   └── ai-rag/             # ❌ Phase 2
├── feature/
│   ├── feature-avatar/     # ❌ Phase 3
│   ├── feature-settings/   # 🟡 Minimal in MVP
│   └── feature-actions/    # ❌ Phase 3
├── services/               # ❌ Phase 2-3
└── system/                 # ❌ Phase 3
```

---

## 📱 MVP Screens

### 1. Chat Screen (Primary)

```
┌──────────────────────────────────────────┐
│  ◀  JARVIS                    ⚙️         │
├──────────────────────────────────────────┤
│                                          │
│  ┌────────────────────────────────────┐  │
│  │ You: What's 2+2?                   │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │ JARVIS: 2+2 equals 4.              │  │
│  │                                    │  │
│  │                    [👍] [✏️ Edit]  │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │ You: Actually, I prefer you say    │  │
│  │ "The answer is 4" instead          │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │ JARVIS: Got it! I'll remember      │  │
│  │ that you prefer "The answer is X"  │  │
│  │ format. Thanks for the feedback!   │  │
│  └────────────────────────────────────┘  │
│                                          │
├──────────────────────────────────────────┤
│  ┌────────────────────────────────────┐  │
│  │  Type a message...            ➤    │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

### 2. Loading Screen

```
┌──────────────────────────────────────────┐
│                                          │
│                                          │
│              🤖 JARVIS                   │
│                                          │
│         Loading AI Model...              │
│         ████████░░░░░░░░░░ 45%           │
│                                          │
│         phi-3.5-mini-instruct            │
│         2.5 GB                           │
│                                          │
│                                          │
└──────────────────────────────────────────┘
```

### 3. Settings Screen (Minimal)

```
┌──────────────────────────────────────────┐
│  ◀  Settings                             │
├──────────────────────────────────────────┤
│                                          │
│  📊 Learning Stats                       │
│  ├── Total conversations: 47             │
│  ├── Corrections received: 12            │
│  ├── Learned preferences: 8              │
│  └── Accuracy rate: 74%                  │
│                                          │
│  ─────────────────────────────────────   │
│                                          │
│  🗄️ Data                                 │
│  [Clear All Data]                        │
│  [Export Learnings]                      │
│                                          │
│  ─────────────────────────────────────   │
│                                          │
│  ℹ️ About                                │
│  Version: 0.1.0-mvp                      │
│  Model: Phi-3.5-mini Q4_K_M              │
│                                          │
└──────────────────────────────────────────┘
```

---

## 🗄️ MVP Database Schema

### Tables Needed

```kotlin
// Core conversation storage
@Entity
data class ConversationEntity(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val updatedAt: Long,
    val title: String?  // Auto-generated from first message
)

@Entity
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String,  // "user" or "assistant"
    val content: String,
    val timestamp: Long,
    val wasEdited: Boolean = false
)

// Learning storage
@Entity
data class CorrectionEntity(
    @PrimaryKey val id: String,
    val originalResponse: String,
    val correctedResponse: String,
    val context: String,
    val timestamp: Long
)

@Entity
data class PreferenceEntity(
    @PrimaryKey val id: String,
    val category: String,
    val key: String,
    val value: String,
    val confidence: Float,
    val learnedAt: Long,
    val updatedAt: Long
)

// Metrics
@Entity
data class DailyMetricsEntity(
    @PrimaryKey val date: String,  // "2026-01-29"
    val totalMessages: Int,
    val corrections: Int,
    val preferencesLearned: Int
)
```

---

## 🔌 MVP AI Integration

### LLM Provider Interface (Minimal)

```kotlin
interface LLMProvider {
    val isLoaded: StateFlow<Boolean>
    val loadingProgress: StateFlow<Float>
    
    suspend fun load(modelPath: String)
    suspend fun unload()
    
    fun generateStream(
        prompt: String,
        systemPrompt: String
    ): Flow<String>
}
```

### System Prompt Template

```kotlin
fun buildSystemPrompt(learnings: Learnings): String = """
You are JARVIS, a helpful AI assistant. You learn from every conversation.

## User Preferences (Learned)
${learnings.preferences.joinToString("\n") { "- ${it.key}: ${it.value}" }}

## Important Corrections (Never repeat these mistakes)
${learnings.corrections.joinToString("\n") { 
    "- ❌ Don't say: \"${it.wrong}\"\n  ✅ Say instead: \"${it.correct}\"" 
}}

## Your Personality
- Helpful and concise
- Learn from feedback
- Admit mistakes gracefully
- Remember user preferences

Respond naturally and helpfully.
""".trimIndent()
```

---

## ✅ MVP Definition of Done

### Feature Complete When:

- [ ] **Chat works** - Can send message, get streaming response
- [ ] **Offline** - Works in airplane mode after model loads
- [ ] **Persists** - Chats survive app restart
- [ ] **Corrections work** - Can edit response, correction saved
- [ ] **Learning works** - Corrections appear in next prompt
- [ ] **Stats visible** - Can see correction rate in settings
- [ ] **No crashes** - Stable for 30 min continuous use
- [ ] **Performance OK** - First token <5 sec on test device

### Quality Gates:

- [ ] All unit tests pass
- [ ] No critical lint warnings
- [ ] APK size < 50 MB (excluding models)
- [ ] Memory usage < 3 GB during inference
- [ ] No ANR on main thread

---

## 📅 MVP Timeline

### Week 1-2: Foundation
- [ ] Project scaffolding (multi-module)
- [ ] Gradle setup with version catalog
- [ ] Core interfaces defined
- [ ] Basic Compose theme

### Week 3-4: AI Integration
- [ ] llama.cpp JNI integration
- [ ] LlamaLLMProvider implementation
- [ ] Model loading with progress
- [ ] Basic generation working

### Week 5-6: Chat UI
- [ ] Chat screen with Compose
- [ ] Message list with streaming
- [ ] Input field with send
- [ ] Conversation persistence

### Week 7-8: Learning System
- [ ] Correction capture UI
- [ ] CorrectionRepository
- [ ] PreferenceExtractor (basic)
- [ ] System prompt injection

### Week 9: Polish & Testing
- [ ] Settings screen
- [ ] Metrics display
- [ ] Bug fixes
- [ ] Performance optimization

### Week 10: MVP Complete
- [ ] Final testing
- [ ] Documentation
- [ ] Demo video
- [ ] Release v0.1.0-mvp

---

## 🎯 Success Metrics

### MVP is Successful If:

| Metric | Target |
|--------|--------|
| First token latency | < 5 seconds |
| Token generation | > 8 tokens/second |
| App start to chat ready | < 30 seconds |
| Correction persistence | 100% (never lose) |
| Crash rate | 0% in normal use |
| Memory peak | < 3.5 GB |

### Learning System Success:

| Metric | Target |
|--------|--------|
| Corrections stored | 100% captured |
| Context injection | Visible in prompts |
| User can see stats | Yes |

---

## 🚀 Post-MVP Roadmap Preview

After MVP success, Phase 2 priorities:

1. **Voice Integration** (STT + TTS)
2. **RAG / Embeddings** (better memory)
3. **Pattern Recognition** (proactive learning)
4. **Better Preference Extraction** (automatic)

Phase 3:
1. **Avatar System**
2. **Device Control**
3. **AccessibilityService**
4. **Widgets**

---

## ⚠️ Scope Creep Prevention

### If Someone Suggests Adding:

| Suggestion | Response |
|------------|----------|
| "Let's add voice!" | "That's Phase 2. Focus on learning system first." |
| "What about avatar?" | "Phase 3. Core chat must work perfectly first." |
| "Can we control device?" | "Phase 3. Need stable AI first." |
| "Add widget?" | "Phase 3. Get the app working first." |

### The Rule:
**If it's not in the P0/P1 list above, it waits until MVP is done.**

---

## 📋 MVP Kickoff Checklist

Before writing code:

- [x] TECH_STACK.md complete
- [x] PROJECT_DECISIONS.md complete
- [x] OPEN_SOURCE_REFERENCES.md complete
- [x] DEVELOPMENT_SETUP.md complete
- [x] MVP_SCOPE.md complete (this doc)
- [ ] Development environment set up
- [ ] Test device ready
- [ ] Models downloaded
- [ ] Ready to scaffold!

---

*Last updated: January 2026*
