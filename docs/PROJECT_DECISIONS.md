# 📋 J.A.R.V.I.S. Project Decisions & Discussion Log

> This document captures all architectural decisions, discussions, and rationale for the J.A.R.V.I.S. project.

---

## 🎯 Project Vision

**JARVIS is an AI that gets smarter every day by learning from its mistakes.**

| Time | Learning State |
|------|----------------|
| Day 1 | Makes mistakes, learns your basics |
| Week 1 | Understands patterns, fewer errors |
| Month 1 | Anticipates needs, rarely wrong |
| Month 6 | Knows you deeply, highly accurate |
| Year 1+ | Like a lifelong companion who never forgets |

The core principle: **Every correction makes JARVIS smarter. Every conversation adds to its knowledge. Accuracy grows continuously without requiring app updates.**

---

## 📅 Decision Timeline

### January 29, 2026 - Project Kickoff

---

## 🎯 Decision 1: Development Framework

### Question
Should we use Flutter (developer's expertise) or Native Kotlin?

### Options Considered

| Option | Pros | Cons |
|--------|------|------|
| **Flutter + Native Modules** | Leverage existing skills, fast UI | ~45% native code anyway, bridge overhead |
| **Full Native Kotlin** | Direct JNI, native services, single language | Learning curve for Kotlin/Compose |
| **Flutter UI + Kotlin Core** | Best of both, clean separation | Two codebases to maintain |

### Decision
✅ **Option 2: Full Native Kotlin + Jetpack Compose**

### Rationale
1. **Direct JNI access** - No Flutter bridge overhead for AI inference
2. **Native Android services** - AccessibilityService, NotificationListener require Kotlin
3. **Single language** - Entire Android codebase in one language
4. **Performance** - Critical for on-device LLM (every millisecond matters)
5. **Jetpack Compose** - Similar to Flutter's declarative style, easier transition

### Impact
- Developer will learn Kotlin + Compose (familiar paradigm)
- Cleaner architecture for AI-heavy operations
- Better debugging experience

---

## 🎯 Decision 2: AI Architecture

### Question
How should we structure the AI layer for maintainability and model swapping?

### Decision
✅ **Modular AI Layer with Interface Abstraction**

### Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
├─────────────────────────────────────────────────────────────┤
│                    AI SERVICE LAYER                         │
│              (JarvisAIService - Orchestrator)               │
├─────────────────────────────────────────────────────────────┤
│    AI ABSTRACTION LAYER (Interfaces Only)                   │
│    LLMProvider | STTProvider | TTSProvider | EmbeddingProvider
├─────────────────────────────────────────────────────────────┤
│    AI IMPLEMENTATION LAYER (Swappable)                      │
│    LlamaLLM | WhisperSTT | PiperTTS | MiniLMEmbedding      │
├─────────────────────────────────────────────────────────────┤
│                    NATIVE LAYER (JNI/C++)                   │
│    llama.cpp | whisper.cpp | onnxruntime                   │
└─────────────────────────────────────────────────────────────┘
```

### Benefits
- Swap LLM models by changing one line in DI config
- Mock AI providers for testing
- Future-proof for better models
- A/B testing between models

---

## 🎯 Decision 3: Project Structure

### Decision
✅ **Multi-Module Gradle Project**

### Module Structure
```
jarvis-ai/
├── app/                    # Main Android app
├── core/                   # Core utilities
├── domain/                 # Business logic (pure Kotlin)
├── data/                   # Data layer (Room, DataStore)
├── ai/                     # AI modules
│   ├── ai-core/           # Interfaces only
│   ├── ai-llama/          # llama.cpp implementation
│   ├── ai-whisper/        # whisper.cpp implementation
│   ├── ai-piper/          # Piper TTS implementation
│   ├── ai-embeddings/     # Embedding implementations
│   └── ai-rag/            # RAG pipeline
├── feature/               # Feature modules
├── services/              # Android services
└── system/                # System integration
```

### Rationale
- Clear separation of concerns
- Independent module compilation
- Easy to test in isolation
- Swappable AI implementations

---

## 🎯 Decision 4: Self-Improving Learning System

### Question
How should JARVIS learn from mistakes and improve accuracy over time?

### Decision
✅ **Continuous Learning Without Model Retraining**

### The Philosophy
JARVIS gets smarter every day through conversation analysis and mistake learning, **without requiring LLM model retraining or app updates**.

### How It Works

```
┌─────────────────────────────────────────────────────────────┐
│                 ACCURACY IMPROVEMENT LOOP                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  User corrects JARVIS → Store correction (HIGH priority)   │
│         ↓                                                   │
│  Extract patterns from conversations                        │
│         ↓                                                   │
│  Build personalized context for LLM prompts                │
│         ↓                                                   │
│  Next response is MORE ACCURATE                            │
│         ↓                                                   │
│  Repeat → Accuracy grows daily                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Learning Priority Hierarchy

| Priority | Type | Example | Weight |
|----------|------|---------|--------|
| 🔴 Highest | User Corrections | "No, I meant X" | 10x |
| 🟠 High | Explicit Preferences | "I prefer casual" | 5x |
| 🟡 Medium | Inferred Patterns | Asks weather at 7am daily | 3x |
| 🟢 Normal | Facts | "My dog is Max" | 1x |

### Expected Accuracy Growth

| Timeline | Accuracy | State |
|----------|----------|-------|
| Week 1 | ~60% | Learning phase, many corrections |
| Month 1 | ~80% | Strong preference understanding |
| Month 3 | ~90% | Rarely needs correction |
| Month 6 | ~95% | Knows you deeply |
| Year 1+ | ~98% | Lifelong companion level |

### Rationale
- No internet needed for improvements
- No waiting for app updates
- User's data improves their own experience
- Privacy preserved - all learning on-device

---

## 🎯 Decision 5: Technology Stack

### Core Stack

| Component | Choice | Version |
|-----------|--------|---------|
| Language | Kotlin | 2.0+ |
| UI | Jetpack Compose | Latest BOM |
| Architecture | MVI + Clean Architecture | - |
| DI | Koin | 4.x |
| Async | Coroutines + Flow | 1.9+ |
| Database | Room + SQLCipher | 2.6+ |
| Vector DB | sqlite-vec or sqlite-vector | Latest |

### AI Stack

| Component | Choice | Model |
|-----------|--------|-------|
| LLM Runtime | llama.cpp | - |
| LLM Model | Phi-3.5-mini-instruct | Q4_K_M (~2.5GB) |
| STT | whisper.cpp | tiny.en (~75MB) |
| TTS | Piper | British male (~50MB) |
| Embeddings | ONNX Runtime | all-MiniLM-L6-v2 (~25MB) |
| Wake Word | Porcupine | Custom "Hey JARVIS" |

---

## 📜 License Compatibility Analysis

### ✅ Fully Open Source - Free to Use

| Project | License | Commercial Use | Modification | Attribution |
|---------|---------|----------------|--------------|-------------|
| **llama.cpp** | MIT | ✅ Yes | ✅ Yes | Required |
| **whisper.cpp** | MIT | ✅ Yes | ✅ Yes | Required |
| **Piper TTS** | MIT | ✅ Yes | ✅ Yes | Required |
| **sqlite-vec** | MIT + Apache-2.0 | ✅ Yes | ✅ Yes | Required |
| **Shizuku** | Apache-2.0 | ✅ Yes | ✅ Yes | Required |
| **Shizuku-API** | MIT | ✅ Yes | ✅ Yes | Required |
| **Jan.ai** | AGPL-3.0 | ⚠️ Limited | ✅ Yes | Required + Source |
| **Koin** | Apache-2.0 | ✅ Yes | ✅ Yes | Required |
| **Room** | Apache-2.0 | ✅ Yes | ✅ Yes | Required |

### ⚠️ Check Before Using

| Project | License | Notes |
|---------|---------|-------|
| **sqlite-vector** | Custom | Free to use, check terms |
| **Porcupine** | Proprietary | Free tier available, paid for commercial |
| **LLM Hub** | Not specified | Reference only, don't copy code directly |
| **Panda (blurr)** | Personal Use | Free for non-commercial, study patterns |
| **zerotap** | Closed Source | Behavior reference only |

### 🤖 AI Model Licenses

| Model | License | Commercial Use |
|-------|---------|----------------|
| **Phi-3.5-mini** | MIT | ✅ Yes |
| **Gemma-2** | Gemma License | ✅ Yes (with terms) |
| **Llama-3.2** | Llama 3.2 License | ✅ Yes (with terms) |
| **Qwen-2.5** | Apache-2.0 | ✅ Yes |
| **Whisper models** | MIT | ✅ Yes |
| **Piper voices** | MIT | ✅ Yes |

---

## 📌 What We CAN Use Freely

### ✅ Core Infrastructure (MIT/Apache-2.0)
```
llama.cpp       → LLM inference engine
whisper.cpp     → Speech-to-text
Piper           → Text-to-speech
sqlite-vec      → Vector search
Shizuku         → System access
Shizuku-API     → API wrapper
```

### ✅ AI Models (MIT/Apache-2.0)
```
Phi-3.5-mini    → Primary LLM (Microsoft, MIT)
Qwen-2.5        → Alternative LLM (Alibaba, Apache-2.0)
Whisper         → STT models (OpenAI, MIT)
Piper voices    → TTS voices (Rhasspy, MIT)
all-MiniLM-L6   → Embeddings (Apache-2.0)
```

### ⚠️ Use as Reference Only
```
LLM Hub         → Study architecture, don't copy code
Jan.ai          → AGPL-3.0, reference patterns only
Llamatik        → Check license before using
```

---

## 🔧 Action Items

### Immediate
- [x] Decide on development framework (Kotlin + Compose)
- [x] Define AI architecture (Modular with interfaces)
- [x] Document technology stack
- [x] Verify license compatibility

### Next Steps
- [ ] Scaffold project structure
- [ ] Set up llama.cpp Android build
- [ ] Create AI provider interfaces
- [ ] Implement basic chat UI

---

## 📚 Reference Documents

| Document | Description |
|----------|-------------|
| [README.md](../README.md) | Project overview and vision |
| [TECH_STACK.md](./TECH_STACK.md) | Detailed technology stack |
| [OPEN_SOURCE_REFERENCES.md](./OPEN_SOURCE_REFERENCES.md) | Open source projects to learn from |

---

## 💬 Discussion Notes

### Flutter vs Native Discussion

**Developer Background:** Flutter developer considering options for J.A.R.V.I.S.

**Key Points Discussed:**
1. Flutter would require ~45% native Kotlin code anyway due to:
   - AccessibilityService (100% native required)
   - NotificationListenerService (100% native required)
   - Shizuku integration (100% native required)
   - JNI bridges for llama.cpp, whisper.cpp

2. Performance considerations:
   - Flutter + Platform Channel adds serialization overhead
   - Native Kotlin has direct JNI access
   - LLM inference is latency-sensitive

3. Final decision: **Full Native Kotlin + Compose**
   - Compose is similar to Flutter (declarative UI)
   - Single language for entire codebase
   - Better for this AI-heavy project

### AI Model Selection

**Requirements:**
- Must run on-device (6GB+ RAM phones)
- Must be <3GB storage
- Must support streaming
- Must handle conversation context

**Selected:** Phi-3.5-mini-instruct (Q4_K_M)
- 3.8B parameters, quantized to ~2.5GB
- Best reasoning capability for size
- MIT license (fully open)
- Microsoft supported

**Alternatives identified:**
- Gemma-2-2B (smaller, 1.5GB)
- Qwen2.5-3B (multilingual)
- Llama-3.2-3B (good all-rounder)

### UI Automation & AI Agents Research

**Projects Researched:**

1. **zerotap** (Closed Source - Google Play Store)
   - AI agent app that controls Android via text/voice commands
   - Uses AccessibilityService (no root/ADB required)
   - Supports multiple LLM providers: Ollama, OpenRouter, OpenAI, Claude, Gemini, DeepSeek
   - BYOK (Bring Your Own Key) model
   - Working on on-device models (Gemma 3n, Qwen)
   - **Insight:** Developer quote: *"Hopefully one day we will have good and reliable on-device LLM"*
   - **JARVIS Advantage:** We're building exactly what zerotap is hoping for - 100% offline!

2. **Panda (blurr)** (Open Source - github.com/Ayush0Chaudhary/blurr)
   - 862+ stars, 117 forks, 99.1% Kotlin
   - Personal AI Phone Operator with multi-agent architecture
   - **Architecture:** Eyes & Hands (Actuator) → Brain (LLM) → Agent (Operator)
   - Uses AccessibilityService for UI automation
   - Can be set as default Assistant (Google Assistant replacement)
   - **License:** Personal Use - Free for non-commercial, commercial requires license
   - **What to Study:**
     - AccessibilityService implementation patterns
     - Screen element hierarchy parsing
     - Touch gesture execution (tap, swipe, type)
     - Multi-agent coordination
   - **Note:** Don't copy code directly - study patterns and implement our own

**Key Learnings:**
- AccessibilityService is the proven approach for UI automation
- No root/ADB needed for most automation tasks
- Multi-agent architecture works well (perception, reasoning, action)
- On-device LLM for UI automation is the next frontier
- JARVIS will be a pioneer in 100% offline AI agents

---

*Last updated: January 29, 2026*
