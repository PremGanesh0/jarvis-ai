# 🛠️ J.A.R.V.I.S. Technology Stack

> **Full Native Kotlin + Jetpack Compose** approach for building a 100% offline, privacy-first AI personal assistant for Android.

---

## � Core Vision: Self-Improving AI

```
┌─────────────────────────────────────────────────────────────┐
│                 THE JARVIS LEARNING PHILOSOPHY              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   Day 1:   JARVIS makes mistakes, learns your basics        │
│   Week 1:  Understands your patterns, fewer errors          │
│   Month 1: Anticipates needs, rarely wrong                  │
│   Month 6: Knows you deeply, highly accurate                │
│   Year 1:  Like a lifelong companion who never forgets      │
│                                                             │
│   📈 ACCURACY GROWS EVERY DAY                               │
│   🧠 LEARNS FROM EVERY MISTAKE                              │
│   🔄 CONTINUOUSLY IMPROVES WITHOUT UPDATES                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Key Principle:** JARVIS doesn't stay static. Every conversation, every correction, every interaction makes it smarter. The model's effective accuracy improves through:

1. **Mistake Learning** - When you correct JARVIS, it never makes that mistake again
2. **Pattern Recognition** - Learns what you need before you ask
3. **Context Accumulation** - More history = better understanding
4. **Preference Refinement** - Constantly fine-tunes responses to your style

---

## �🎯 Architecture Philosophy

### Why Native Kotlin?
- **Direct JNI access** to C++ AI libraries (no Flutter bridge overhead)
- **Native Android services** (Accessibility, NotificationListener, Shizuku)
- **Single language** for entire Android codebase
- **Better debugging** - no Dart/Kotlin boundary issues
- **Optimal performance** for on-device AI inference

### Core Principles
1. **Modular AI Layer** - All AI components are abstracted behind interfaces
2. **Clean Architecture** - Separation of concerns with clear boundaries
3. **Dependency Inversion** - Core modules don't depend on implementations
4. **Easy Model Swapping** - Change LLM/STT/TTS without touching business logic
5. **Continuous Learning** - AI learns from EVERY conversation and improves daily
6. **Mistake-Driven Growth** - Corrections are the highest-value learning signals
7. **Accumulating Intelligence** - The longer you use JARVIS, the smarter it gets

---

## 🏗️ Core Architecture

| Layer | Choice | Rationale |
|-------|--------|-----------|
| **Language** | **Kotlin 2.0+** | Modern, null-safe, coroutines |
| **UI** | **Jetpack Compose + Material 3** | Declarative, animation-friendly |
| **Architecture** | **MVI + Clean Architecture** | Unidirectional flow, testable |
| **DI** | **Koin 4.x** | Lightweight, KMP-ready |
| **Async** | **Coroutines + Flow** | Structured concurrency, streaming |
| **Build** | **Gradle KTS + Version Catalogs** | Type-safe, maintainable |

---

## 🧠 Modular AI Architecture

### The Key Design: AI Abstraction Layer

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
│         (ViewModels, UseCases, UI Components)               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                    AI SERVICE LAYER                         │
│    ┌─────────────────────────────────────────────────┐     │
│    │              JarvisAIService                     │     │
│    │  (Orchestrates all AI components)                │     │
│    └─────────────────────────────────────────────────┘     │
│              │           │           │                      │
├──────────────┼───────────┼───────────┼──────────────────────┤
│              ▼           ▼           ▼                      │
│    AI ABSTRACTION LAYER (Interfaces Only)                   │
│    ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   │
│    │  LLM    │   │   STT   │   │   TTS   │   │Embedding│   │
│    │Provider │   │Provider │   │Provider │   │Provider │   │
│    └────┬────┘   └────┬────┘   └────┬────┘   └────┬────┘   │
│         │             │             │             │         │
├─────────┼─────────────┼─────────────┼─────────────┼─────────┤
│         ▼             ▼             ▼             ▼         │
│    AI IMPLEMENTATION LAYER (Swappable)                      │
│    ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   │
│    │ Llama   │   │ Whisper │   │  Piper  │   │MiniLM   │   │
│    │  .cpp   │   │  .cpp   │   │  ONNX   │   │  ONNX   │   │
│    └─────────┘   └─────────┘   └─────────┘   └─────────┘   │
│         │             │             │             │         │
├─────────┼─────────────┼─────────────┼─────────────┼─────────┤
│         ▼             ▼             ▼             ▼         │
│                    NATIVE LAYER (JNI/C++)                   │
│    ┌─────────────────────────────────────────────────┐     │
│    │  llama.cpp  │  whisper.cpp  │  onnxruntime      │     │
│    └─────────────────────────────────────────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Why This Design?

| Benefit | Description |
|---------|-------------|
| **Swap Models Easily** | Replace Phi-3 with Gemma/Qwen by implementing interface |
| **Test in Isolation** | Mock AI providers for unit tests |
| **A/B Testing** | Run two LLMs side-by-side, compare |
| **Graceful Fallbacks** | If one model fails, switch to another |
| **Future-Proof** | New better model? Just add implementation |

---

## 🔌 AI Provider Interfaces

### LLM Provider (Core Intelligence)

```kotlin
// Domain layer - no implementation details
interface LLMProvider {
    val modelInfo: ModelInfo
    val isLoaded: StateFlow<Boolean>

    suspend fun load(modelPath: String, config: LLMConfig)
    suspend fun unload()

    // Single response
    suspend fun generate(
        prompt: String,
        params: GenerationParams = GenerationParams()
    ): Result<String>

    // Streaming response (critical for UX)
    fun generateStream(
        prompt: String,
        params: GenerationParams = GenerationParams()
    ): Flow<String>

    // With conversation history
    fun chat(
        messages: List<ChatMessage>,
        params: GenerationParams = GenerationParams()
    ): Flow<String>

    // Cancel ongoing generation
    fun cancel()
}

data class ModelInfo(
    val name: String,
    val parameterCount: Long,
    val quantization: String,
    val contextLength: Int,
    val capabilities: Set<Capability>
)

data class GenerationParams(
    val maxTokens: Int = 512,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val repeatPenalty: Float = 1.1f,
    val stopSequences: List<String> = emptyList()
)

enum class Capability { CHAT, REASONING, CODE, FUNCTION_CALLING }
```

### STT Provider (Speech-to-Text)

```kotlin
interface STTProvider {
    val isLoaded: StateFlow<Boolean>
    val isListening: StateFlow<Boolean>

    suspend fun load(modelPath: String, config: STTConfig)
    suspend fun unload()

    // Transcribe audio file
    suspend fun transcribe(audioData: ByteArray): Result<Transcription>

    // Real-time streaming transcription
    fun transcribeStream(audioFlow: Flow<ByteArray>): Flow<TranscriptionChunk>

    // Start/stop listening from microphone
    suspend fun startListening()
    suspend fun stopListening(): Result<Transcription>
}

data class Transcription(
    val text: String,
    val language: String,
    val confidence: Float,
    val segments: List<TranscriptionSegment>
)
```

### TTS Provider (Text-to-Speech)

```kotlin
interface TTSProvider {
    val isLoaded: StateFlow<Boolean>
    val isSpeaking: StateFlow<Boolean>
    val availableVoices: List<VoiceInfo>

    suspend fun load(modelPath: String, config: TTSConfig)
    suspend fun unload()

    // Generate audio bytes
    suspend fun synthesize(text: String, voice: VoiceId): Result<ByteArray>

    // Stream audio for long text
    fun synthesizeStream(text: String, voice: VoiceId): Flow<ByteArray>

    // Direct playback
    suspend fun speak(text: String, voice: VoiceId)
    fun stop()
}

data class VoiceInfo(
    val id: VoiceId,
    val name: String,
    val language: String,
    val gender: Gender,
    val style: VoiceStyle  // FORMAL, CASUAL, WARM, etc.
)
```

### Embedding Provider (RAG/Semantic Search)

```kotlin
interface EmbeddingProvider {
    val dimensions: Int
    val isLoaded: StateFlow<Boolean>

    suspend fun load(modelPath: String)
    suspend fun unload()

    suspend fun embed(text: String): Result<FloatArray>
    suspend fun embedBatch(texts: List<String>): Result<List<FloatArray>>

    // Similarity between two texts
    suspend fun similarity(text1: String, text2: String): Float
}
```

---

## 🧩 AI Implementation Examples

### LlamaProvider Implementation

```kotlin
// Implementation layer - depends on native llama.cpp
class LlamaLLMProvider(
    private val llamaJni: LlamaJni,  // JNI bridge
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : LLMProvider {

    private val _isLoaded = MutableStateFlow(false)
    override val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private var contextHandle: Long = 0L

    override suspend fun load(modelPath: String, config: LLMConfig) {
        withContext(dispatcher) {
            contextHandle = llamaJni.loadModel(
                modelPath = modelPath,
                nCtx = config.contextLength,
                nGpuLayers = config.gpuLayers,
                useMmap = config.useMmap
            )
            _isLoaded.value = true
        }
    }

    override fun generateStream(
        prompt: String,
        params: GenerationParams
    ): Flow<String> = callbackFlow {
        llamaJni.generateStreaming(
            context = contextHandle,
            prompt = prompt,
            maxTokens = params.maxTokens,
            temperature = params.temperature,
            callback = { token ->
                trySend(token)
                !isClosedForSend  // continue if channel open
            }
        )
        close()
    }.flowOn(dispatcher)
}
```

### Easy Model Swapping via DI

```kotlin
// In Koin module - swap implementation here
val aiModule = module {

    // LLM - Change this ONE line to swap models
    single<LLMProvider> {
        LlamaLLMProvider(get())  // Current: llama.cpp
        // GemmaLLMProvider(get())  // Alternative: Gemma
        // QwenLLMProvider(get())   // Alternative: Qwen
    }

    // STT
    single<STTProvider> {
        WhisperSTTProvider(get())
        // VoskSTTProvider(get())  // Alternative
    }

    // TTS
    single<TTSProvider> {
        PiperTTSProvider(get())
        // CoquiTTSProvider(get())  // Alternative
    }

    // Embeddings
    single<EmbeddingProvider> {
        MiniLMEmbeddingProvider(get())
        // BGEEmbeddingProvider(get())  // Alternative
    }
}
```

---

## 📁 Multi-Module Project Structure

```
jarvis-ai/
│
├── 📦 app/                           # Main Android app
│   ├── src/main/
│   │   ├── kotlin/com/jarvis/app/
│   │   │   ├── JarvisApp.kt          # Application class
│   │   │   ├── MainActivity.kt
│   │   │   ├── di/                   # Koin modules
│   │   │   └── navigation/           # Nav graph
│   │   └── res/
│   └── build.gradle.kts
│
├── 📦 core/                          # Core utilities (no Android deps)
│   ├── core-common/                  # Kotlin utilities, Result types
│   ├── core-model/                   # Domain models, entities
│   └── core-testing/                 # Test utilities
│
├── 📦 domain/                        # Business logic (pure Kotlin)
│   ├── src/main/kotlin/com/jarvis/domain/
│   │   ├── model/                    # Domain entities
│   │   │   ├── ChatMessage.kt
│   │   │   ├── Memory.kt
│   │   │   └── UserProfile.kt
│   │   ├── repository/               # Repository interfaces
│   │   │   ├── ConversationRepository.kt
│   │   │   ├── MemoryRepository.kt
│   │   │   └── SettingsRepository.kt
│   │   └── usecase/                  # Business use cases
│   │       ├── chat/
│   │       │   ├── SendMessageUseCase.kt
│   │       │   └── GetConversationUseCase.kt
│   │       ├── memory/
│   │       │   ├── StoreMemoryUseCase.kt
│   │       │   └── RecallMemoryUseCase.kt
│   │       └── action/
│   │           └── ExecuteActionUseCase.kt
│   └── build.gradle.kts
│
├── 📦 data/                          # Data layer implementations
│   ├── src/main/kotlin/com/jarvis/data/
│   │   ├── local/
│   │   │   ├── database/             # Room database
│   │   │   │   ├── JarvisDatabase.kt
│   │   │   │   ├── dao/
│   │   │   │   └── entity/
│   │   │   ├── datastore/            # Preferences
│   │   │   └── vector/               # sqlite-vec integration
│   │   ├── repository/               # Repository implementations
│   │   └── mapper/                   # Entity <-> Domain mappers
│   └── build.gradle.kts
│
├── 📦 ai/                            # 🔥 AI LAYER (MODULAR)
│   │
│   ├── ai-core/                      # AI abstractions (interfaces only)
│   │   ├── src/main/kotlin/com/jarvis/ai/core/
│   │   │   ├── llm/
│   │   │   │   ├── LLMProvider.kt           # Interface
│   │   │   │   ├── LLMConfig.kt
│   │   │   │   └── GenerationParams.kt
│   │   │   ├── stt/
│   │   │   │   ├── STTProvider.kt           # Interface
│   │   │   │   └── Transcription.kt
│   │   │   ├── tts/
│   │   │   │   ├── TTSProvider.kt           # Interface
│   │   │   │   └── VoiceInfo.kt
│   │   │   ├── embedding/
│   │   │   │   └── EmbeddingProvider.kt     # Interface
│   │   │   └── wakeword/
│   │   │       └── WakeWordDetector.kt      # Interface
│   │   └── build.gradle.kts          # Pure Kotlin, no deps
│   │
│   ├── ai-llama/                     # llama.cpp implementation
│   │   ├── src/main/
│   │   │   ├── kotlin/com/jarvis/ai/llama/
│   │   │   │   ├── LlamaLLMProvider.kt
│   │   │   │   ├── LlamaJni.kt       # JNI interface
│   │   │   │   └── LlamaConfig.kt
│   │   │   └── cpp/
│   │   │       ├── llama-jni.cpp     # JNI bridge
│   │   │       └── CMakeLists.txt
│   │   └── build.gradle.kts
│   │
│   ├── ai-whisper/                   # whisper.cpp implementation
│   │   ├── src/main/
│   │   │   ├── kotlin/com/jarvis/ai/whisper/
│   │   │   │   ├── WhisperSTTProvider.kt
│   │   │   │   └── WhisperJni.kt
│   │   │   └── cpp/
│   │   │       ├── whisper-jni.cpp
│   │   │       └── CMakeLists.txt
│   │   └── build.gradle.kts
│   │
│   ├── ai-piper/                     # Piper TTS implementation
│   │   ├── src/main/kotlin/com/jarvis/ai/piper/
│   │   │   ├── PiperTTSProvider.kt
│   │   │   └── PiperOnnx.kt
│   │   └── build.gradle.kts
│   │
│   ├── ai-embeddings/                # Embedding implementations
│   │   ├── src/main/kotlin/com/jarvis/ai/embeddings/
│   │   │   ├── MiniLMEmbeddingProvider.kt
│   │   │   └── OnnxEmbedding.kt
│   │   └── build.gradle.kts
│   │
│   └── ai-rag/                       # RAG pipeline
│       ├── src/main/kotlin/com/jarvis/ai/rag/
│       │   ├── RAGPipeline.kt
│       │   ├── DocumentChunker.kt
│       │   └── ContextBuilder.kt
│       └── build.gradle.kts
│
├── 📦 learning/                      # 🧠 CONVERSATIONAL LEARNING
│   ├── src/main/kotlin/com/jarvis/learning/
│   │   ├── ConversationAnalyzer.kt   # Extract learnings from chats
│   │   ├── UserPreferenceTracker.kt  # Track user preferences
│   │   ├── PatternRecognizer.kt      # Identify user patterns
│   │   ├── LearningRepository.kt     # Store learned knowledge
│   │   └── PersonalizationEngine.kt  # Apply learnings to responses
│   └── build.gradle.kts
│
├── 📦 feature/                       # Feature modules (UI + logic)
│   ├── feature-chat/                 # Chat screen
│   │   ├── src/main/kotlin/com/jarvis/feature/chat/
│   │   │   ├── ChatScreen.kt
│   │   │   ├── ChatViewModel.kt
│   │   │   └── components/
│   │   └── build.gradle.kts
│   │
│   ├── feature-avatar/               # Avatar display & customization
│   ├── feature-settings/             # Settings screens
│   ├── feature-memory/               # Memory browser
│   └── feature-actions/              # Action shortcuts
│
├── 📦 services/                      # Android services
│   ├── src/main/kotlin/com/jarvis/services/
│   │   ├── JarvisService.kt          # Foreground service
│   │   ├── JarvisAccessibilityService.kt
│   │   ├── JarvisNotificationListener.kt
│   │   └── WakeWordService.kt
│   └── build.gradle.kts
│
├── 📦 system/                        # System integration
│   ├── system-actions/               # Device controls
│   ├── system-collectors/            # Data collectors
│   └── system-shizuku/               # Shizuku integration
│
├── 📁 models/                        # AI models (git-ignored)
│   ├── llm/
│   │   └── phi-3.5-mini-q4_k_m.gguf
│   ├── stt/
│   │   └── whisper-tiny.en.bin
│   ├── tts/
│   │   └── en-gb-southern_english_male-medium.onnx
│   └── embeddings/
│       └── all-minilm-l6-v2.onnx
│
├── 📁 build-logic/                   # Convention plugins
│   └── convention/
│
├── gradle/
│   └── libs.versions.toml            # Version catalog
│
├── settings.gradle.kts
└── build.gradle.kts
```

---

## 🧠 AI Stack Components

| Component | Implementation | Model | Size |
|-----------|----------------|-------|------|
| **LLM** | llama.cpp (JNI) | Phi-3.5-mini-instruct Q4_K_M | ~2.5 GB |
| **STT** | whisper.cpp (JNI) | whisper-tiny.en | ~75 MB |
| **TTS** | Piper (ONNX) | en-gb-southern_english_male | ~50 MB |
| **Embeddings** | ONNX Runtime | all-MiniLM-L6-v2 | ~25 MB |
| **Wake Word** | Porcupine | Custom "Hey JARVIS" | ~2 MB |

### Model Swap Options

| Slot | Default | Alternatives |
|------|---------|--------------|
| **LLM** | Phi-3.5-mini | Gemma-2-2B, Qwen2.5-3B, Llama-3.2-3B |
| **STT** | Whisper tiny.en | Whisper small, Vosk |
| **TTS** | Piper (British) | Piper (other voices), Coqui |
| **Embeddings** | MiniLM-L6-v2 | BGE-small, Nomic-embed |

---

## 💾 Data Layer

| Component | Choice | Why |
|-----------|--------|-----|
| **Database** | **Room + SQLite** | Type-safe, reactive, Kotlin-first |
| **Vector Extension** | **sqlite-vec** | Native SQLite extension, no extra server |
| **Encryption** | **SQLCipher** | AES-256, transparent encryption |
| **Preferences** | **DataStore (Proto)** | Type-safe, async, coroutines support |
| **File Storage** | **Encrypted File** (Jetpack Security) | For model files, exports |

### Database Schema

```kotlin
@Entity
data class ConversationEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val userMessage: String,
    val assistantMessage: String,
    val emotionalContext: String?,
    val embeddingId: String?
)

@Entity
data class MemoryEntity(
    @PrimaryKey val id: String,
    val type: MemoryType,  // FACT, EVENT, PREFERENCE, RELATIONSHIP
    val content: String,
    val importance: Float,
    val createdAt: Long,
    val lastAccessedAt: Long,
    val accessCount: Int
)

@Entity
data class EmbeddingEntity(
    @PrimaryKey val id: String,
    val sourceType: String,  // CONVERSATION, MEMORY, SMS, etc.
    val sourceId: String,
    val vector: ByteArray,   // Serialized FloatArray
    val createdAt: Long
)

// 🧠 CONVERSATIONAL LEARNING ENTITIES
@Entity
data class LearnedPreferenceEntity(
    @PrimaryKey val id: String,
    val category: String,    // COMMUNICATION_STYLE, TOPIC, SCHEDULE, etc.
    val key: String,         // e.g., "preferred_greeting", "wake_time"
    val value: String,       // e.g., "casual", "07:00"
    val confidence: Float,   // 0.0 to 1.0 based on frequency
    val learnedFrom: Int,    // Number of conversations this was derived from
    val createdAt: Long,
    val updatedAt: Long
)

@Entity
data class ConversationPatternEntity(
    @PrimaryKey val id: String,
    val patternType: String, // QUESTION_TYPE, TIME_PREFERENCE, MOOD_PATTERN
    val description: String, // e.g., "User asks about weather in morning"
    val frequency: Int,      // Times this pattern was observed
    val lastOccurrence: Long,
    val actionSuggestion: String? // Proactive action to take
)

@Entity
data class FeedbackEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val responseId: String,
    val feedbackType: FeedbackType, // POSITIVE, NEGATIVE, CORRECTION
    val correctedResponse: String?, // If user corrected JARVIS
    val timestamp: Long
)
```

---

## 🧠 Conversational Learning System

### Overview
JARVIS learns and improves from every conversation without requiring model fine-tuning. This is achieved through:

1. **Conversation Analysis** - Extract preferences, facts, and patterns from each chat
2. **Persistent Memory** - Store learnings in encrypted local database
3. **Context Enrichment** - Inject learned knowledge into LLM prompts
4. **Feedback Loop** - User corrections become high-priority learnings

### Learning Provider Interface

```kotlin
interface LearningProvider {

    // Analyze conversation and extract learnings
    suspend fun analyzeConversation(
        messages: List<ChatMessage>
    ): List<Learning>

    // Store a learned preference
    suspend fun storePreference(
        category: PreferenceCategory,
        key: String,
        value: String,
        confidence: Float
    )

    // Record user correction (highest priority learning)
    suspend fun recordCorrection(
        originalResponse: String,
        correctedResponse: String,
        context: String
    )

    // Get all preferences for context enrichment
    suspend fun getPreferences(
        categories: Set<PreferenceCategory>? = null
    ): List<LearnedPreference>

    // Identify patterns from conversation history
    suspend fun detectPatterns(): List<ConversationPattern>

    // Get proactive suggestions based on patterns
    suspend fun getProactiveSuggestions(
        currentContext: Context
    ): List<ProactiveSuggestion>
}

data class Learning(
    val type: LearningType,  // PREFERENCE, FACT, PATTERN, CORRECTION
    val content: String,
    val confidence: Float,
    val source: String       // Which conversation extracted this
)

enum class PreferenceCategory {
    COMMUNICATION_STYLE,    // Formal/casual, verbose/brief
    TIME_PREFERENCES,       // Wake time, busy hours
    TOPICS_OF_INTEREST,     // Tech, sports, music
    PERSONAL_INFO,          // Birthday, family names
    WORK_RELATED,           // Job, meetings, deadlines
    BEHAVIOR_PATTERNS       // Morning routines, habits
}
```

### How Learning Works

```kotlin
// After each conversation, analyze and learn
class ConversationAnalyzer(
    private val llmProvider: LLMProvider,
    private val learningRepo: LearningRepository
) {

    suspend fun analyze(conversation: Conversation) {
        // Use LLM to extract structured learnings
        val extractionPrompt = buildExtractionPrompt(conversation)

        val learnings = llmProvider.generate(
            prompt = extractionPrompt,
            params = GenerationParams(temperature = 0.1f)  // Low temp for accuracy
        ).parseAsLearnings()

        // Store each learning with confidence scoring
        learnings.forEach { learning ->
            when (learning.type) {
                LearningType.PREFERENCE -> {
                    learningRepo.upsertPreference(
                        key = learning.key,
                        value = learning.value,
                        confidence = calculateConfidence(learning)
                    )
                }
                LearningType.FACT -> {
                    learningRepo.storeFact(learning)
                }
                LearningType.CORRECTION -> {
                    // Corrections have highest priority
                    learningRepo.storeCorrection(
                        learning,
                        priority = Priority.HIGH
                    )
                }
            }
        }
    }

    private fun calculateConfidence(learning: Learning): Float {
        // Confidence increases with repetition
        val existingLearning = learningRepo.findSimilar(learning)
        return if (existingLearning != null) {
            minOf(1.0f, existingLearning.confidence + 0.1f)
        } else {
            0.3f  // Initial confidence
        }
    }
}
```

### Context Enrichment

```kotlin
// Before generating response, enrich context with learnings
class PersonalizationEngine(
    private val learningProvider: LearningProvider
) {

    suspend fun enrichContext(
        baseContext: String,
        userMessage: String
    ): String {
        val preferences = learningProvider.getPreferences()
        val patterns = learningProvider.detectPatterns()
        val corrections = learningProvider.getRecentCorrections()

        return buildString {
            appendLine("## User Profile (Learned from conversations)")
            appendLine()

            // Add communication style
            preferences.filter { it.category == COMMUNICATION_STYLE }
                .forEach { appendLine("- ${it.key}: ${it.value}") }

            // Add relevant facts
            appendLine()
            appendLine("## Known Facts")
            preferences.filter { it.category == PERSONAL_INFO }
                .forEach { appendLine("- ${it.key}: ${it.value}") }

            // Add corrections (avoid previous mistakes)
            if (corrections.isNotEmpty()) {
                appendLine()
                appendLine("## Important: Previous Corrections")
                corrections.forEach {
                    appendLine("- Don't say: \"${it.wrong}\" → Say: \"${it.correct}\"")
                }
            }

            appendLine()
            appendLine("## Current Conversation")
            append(baseContext)
        }
    }
}
```

### Learning Categories

| Category | Example Learnings |
|----------|-------------------|
| **Communication Style** | "User prefers brief responses", "User likes technical details" |
| **Time Preferences** | "User wakes at 7am", "User is busy 9-5 on weekdays" |
| **Personal Info** | "User's birthday is March 15", "User has a dog named Max" |
| **Topics of Interest** | "User interested in AI, Flutter, and football" |
| **Work Related** | "User works as software developer", "Weekly meeting on Monday" |
| **Corrections** | "Don't call user 'sir', they prefer casual tone" |

---

## � Accuracy Growth System

### How JARVIS Gets Smarter Every Day

```
┌─────────────────────────────────────────────────────────────┐
│                  ACCURACY IMPROVEMENT LOOP                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   User Interaction                                          │
│        │                                                    │
│        ▼                                                    │
│   ┌─────────────────────────────────────────┐              │
│   │          JARVIS Response                │              │
│   └─────────────────────────────────────────┘              │
│        │                     │                              │
│        ▼                     ▼                              │
│   ✅ Accepted            ❌ Corrected                       │
│        │                     │                              │
│        ▼                     ▼                              │
│   Reinforce            Learn Mistake                        │
│   Pattern              (HIGH PRIORITY)                      │
│        │                     │                              │
│        └─────────┬───────────┘                              │
│                  ▼                                          │
│   ┌─────────────────────────────────────────┐              │
│   │      Knowledge Base Updated             │              │
│   │   • Preferences refined                 │              │
│   │   • Corrections stored                  │              │
│   │   • Patterns strengthened               │              │
│   └─────────────────────────────────────────┘              │
│                  │                                          │
│                  ▼                                          │
│   📈 NEXT RESPONSE IS MORE ACCURATE                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Mistake Learning System

```kotlin
// When user corrects JARVIS, this is GOLD for learning
data class Correction(
    val id: String,
    val originalResponse: String,    // What JARVIS said wrong
    val correctedResponse: String,   // What user wanted
    val context: String,             // Conversation context
    val timestamp: Long,
    val similarityHash: String       // To find similar situations
)

class MistakeLearner(
    private val correctionRepo: CorrectionRepository,
    private val embeddingProvider: EmbeddingProvider
) {

    suspend fun learnFromMistake(
        wrongResponse: String,
        rightResponse: String,
        context: List<ChatMessage>
    ) {
        // Store the correction with high priority
        val correction = Correction(
            originalResponse = wrongResponse,
            correctedResponse = rightResponse,
            context = context.toContextString(),
            timestamp = System.currentTimeMillis()
        )

        // Generate embedding for this correction context
        val embedding = embeddingProvider.embed(correction.context)

        // Store with embedding for future retrieval
        correctionRepo.storeWithEmbedding(correction, embedding)

        // Also extract general rule if possible
        extractGeneralRule(correction)?.let { rule ->
            correctionRepo.storeRule(rule)
        }
    }

    // Before each response, check for similar past mistakes
    suspend fun getRelevantCorrections(
        currentContext: String
    ): List<Correction> {
        val contextEmbedding = embeddingProvider.embed(currentContext)
        return correctionRepo.findSimilar(contextEmbedding, limit = 5)
    }
}
```

### Accuracy Metrics & Tracking

```kotlin
data class AccuracyMetrics(
    val totalInteractions: Int,
    val correctionsReceived: Int,
    val correctionRate: Float,           // Lower is better
    val averageConfidence: Float,
    val learningsAccumulated: Int,
    val patternsRecognized: Int,
    val daysSinceStart: Int
)

class AccuracyTracker {

    // Track accuracy improvement over time
    fun getAccuracyTrend(days: Int = 30): List<DailyAccuracy> {
        return metricsRepo.getDailyMetrics(days).map { metrics ->
            DailyAccuracy(
                date = metrics.date,
                correctionRate = metrics.corrections.toFloat() / metrics.interactions,
                confidence = metrics.averageConfidence
            )
        }
    }

    // Expected: correction rate DECREASES over time
    // Expected: confidence INCREASES over time
}
```

### The Learning Hierarchy

| Priority | Learning Type | Example | Weight |
|----------|---------------|---------|--------|
| 🔴 **Highest** | User Corrections | "No, I meant X not Y" | 10x |
| 🟠 **High** | Explicit Preferences | "I prefer casual tone" | 5x |
| 🟡 **Medium** | Inferred Patterns | User always asks weather at 7am | 3x |
| 🟢 **Normal** | Conversation Facts | "My dog's name is Max" | 1x |
| 🔵 **Low** | Weak Signals | User seemed satisfied | 0.5x |

### Expected Accuracy Growth

```
Week 1:  ~60% accuracy (learning phase)
         - Many corrections expected
         - Building initial profile

Week 2:  ~70% accuracy
         - Basic patterns recognized
         - Common mistakes eliminated

Month 1: ~80% accuracy
         - Strong preference understanding
         - Proactive suggestions working

Month 3: ~90% accuracy
         - Deep understanding
         - Rarely needs correction

Month 6+: ~95% accuracy
         - Knows you better than most apps
         - Anticipates needs correctly

Year 1+: ~98% accuracy
         - Lifelong companion level
         - Corrections are rare events
```

### Key Insight: No Model Retraining Needed

JARVIS improves **without retraining the LLM model**. How?

1. **Context Enrichment** - Inject learned knowledge into every prompt
2. **RAG Retrieval** - Find relevant past interactions and corrections
3. **Correction Injection** - "Don't say X, say Y" included in system prompt
4. **Preference Weighting** - High-confidence learnings affect response style

```kotlin
// The magic happens in prompt construction
fun buildPrompt(
    userMessage: String,
    conversationHistory: List<ChatMessage>,
    learnings: Learnings
): String = buildString {

    // System context with learnings
    appendLine("## About the User (Learned over ${learnings.daysSinceStart} days)")
    learnings.preferences.forEach {
        appendLine("- ${it.key}: ${it.value} (confidence: ${it.confidence})")
    }

    // Critical: Include corrections
    if (learnings.corrections.isNotEmpty()) {
        appendLine()
        appendLine("## IMPORTANT - Past Corrections (Never repeat these mistakes)")
        learnings.corrections.forEach {
            appendLine("- ❌ Wrong: \"${it.wrong}\"")
            appendLine("  ✅ Correct: \"${it.right}\"")
        }
    }

    // Patterns for proactive behavior
    if (learnings.patterns.isNotEmpty()) {
        appendLine()
        appendLine("## Recognized Patterns")
        learnings.patterns.forEach {
            appendLine("- ${it.description}")
        }
    }

    // Current conversation
    appendLine()
    appendLine("## Conversation")
    conversationHistory.forEach { msg ->
        appendLine("${msg.role}: ${msg.content}")
    }
    appendLine("User: $userMessage")
    appendLine("Assistant:")
}
```

---

## �👤 Avatar System

| Component | Choice | Why |
|-----------|--------|-----|
| **2D Rendering** | **Compose Canvas + Lottie** | Native Compose, smooth animations |
| **Expressions** | **Rive** | State machine animations, small files |
| **Assets** | **Pre-generated sets** | Avoid on-device generation (too heavy) |

### Expression State Machine

```kotlin
enum class AvatarExpression {
    NEUTRAL,
    HAPPY,
    THINKING,
    CONCERNED,
    EXCITED,
    PROUD,
    CALM,
    LISTENING
}

class AvatarExpressionEngine {
    fun mapEmotionToExpression(emotion: DetectedEmotion): AvatarExpression
    fun getTransitionAnimation(from: AvatarExpression, to: AvatarExpression): Animation
}
```

---

## ⚡ System Integration

| Component | Choice | Why |
|-----------|--------|-----|
| **Root-like Access** | **Shizuku** | ADB-level without root |
| **UI Automation** | **AccessibilityService** | Required for app control |
| **Notifications** | **NotificationListenerService** | Read all notifications |
| **Background** | **Foreground Service + WorkManager** | Reliable, battery-friendly |
| **Sensors** | **SensorManager + Fused Location** | Comprehensive data |

---

## 🗣️ Voice Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                     VOICE ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Microphone → VAD (Silero) → Wake Word (Porcupine)         │
│       │                            │                        │
│       ▼                            ▼                        │
│  Audio Buffer              Trigger Listening                │
│       │                            │                        │
│       ▼                            ▼                        │
│  Whisper.cpp (tiny.en) ←── Record until silence            │
│       │                                                     │
│       ▼                                                     │
│  Text → LLM → Response Text                                │
│                   │                                         │
│                   ▼                                         │
│             Piper TTS → Speaker                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Dependencies (Version Catalog)

```toml
# gradle/libs.versions.toml

[versions]
kotlin = "2.0.21"
agp = "8.7.3"
ksp = "2.0.21-1.0.28"
coroutines = "1.9.0"
compose-bom = "2024.12.01"
koin = "4.0.0"
room = "2.6.1"
onnxruntime = "1.19.0"
shizuku = "13.1.5"

[libraries]
# Kotlin
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
kotlinx-coroutines = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-serialization = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.7.3" }

# Compose
compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose-bom" }
compose-material3 = { module = "androidx.compose.material3:material3" }
compose-ui-tooling = { module = "androidx.compose.ui:ui-tooling" }
compose-navigation = { module = "androidx.navigation:navigation-compose", version = "2.8.5" }

# Koin
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-androidx-compose", version.ref = "koin" }

# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }

# Database
sqlcipher = { module = "net.zetetic:android-database-sqlcipher", version = "4.6.1" }

# DataStore
datastore = { module = "androidx.datastore:datastore", version = "1.1.1" }
datastore-preferences = { module = "androidx.datastore:datastore-preferences", version = "1.1.1" }

# ML/AI
onnxruntime = { module = "com.microsoft.onnxruntime:onnxruntime-android", version.ref = "onnxruntime" }

# System
shizuku-api = { module = "dev.rikka.shizuku:api", version.ref = "shizuku" }
shizuku-provider = { module = "dev.rikka.shizuku:provider", version.ref = "shizuku" }

# Security
security-crypto = { module = "androidx.security:security-crypto", version = "1.1.0-alpha06" }

# Animation
lottie-compose = { module = "com.airbnb.android:lottie-compose", version = "6.6.2" }

# Testing
junit = { module = "junit:junit", version = "4.13.2" }
mockk = { module = "io.mockk:mockk", version = "1.13.13" }
turbine = { module = "app.cash.turbine:turbine", version = "1.2.0" }
coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutines" }

[bundles]
compose = ["compose-material3", "compose-ui-tooling", "compose-navigation"]
room = ["room-runtime", "room-ktx"]
koin = ["koin-android", "koin-compose"]
testing = ["junit", "mockk", "turbine", "coroutines-test"]

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

---

## 🧪 Testing Strategy

### AI Layer Testing

```kotlin
// Mock LLM provider for tests
class FakeLLMProvider : LLMProvider {
    var predefinedResponses = mutableListOf<String>()

    override fun generateStream(prompt: String, params: GenerationParams) = flow {
        predefinedResponses.removeFirstOrNull()?.let {
            it.split(" ").forEach { word ->
                emit("$word ")
                delay(10)  // Simulate streaming
            }
        }
    }
}

// Test use case with fake AI
class SendMessageUseCaseTest {
    private val fakeLLM = FakeLLMProvider()
    private val useCase = SendMessageUseCase(fakeLLM, fakeMemory, fakeRAG)

    @Test
    fun `message generates response with context`() = runTest {
        fakeLLM.predefinedResponses.add("Hello, how can I help you today?")

        val responses = useCase("Hi JARVIS").toList()

        assertThat(responses.joinToString("")).contains("Hello")
    }
}
```

### Test Coverage Goals

| Layer | Coverage Target | Focus |
|-------|-----------------|-------|
| **Domain (UseCases)** | 90%+ | Business logic |
| **AI Interfaces** | 80%+ | Contract tests |
| **Repository** | 80%+ | Data flow |
| **ViewModel** | 70%+ | State management |
| **UI** | 50%+ | Critical flows |

---

## 🚀 Development Phases (Updated)

### Phase 1: Foundation (Weeks 1-2) 🏗️
- [ ] Multi-module project setup
- [ ] Version catalog + convention plugins
- [ ] Core interfaces (LLMProvider, STTProvider, TTSProvider)
- [ ] Koin DI configuration
- [ ] Basic Compose theme + navigation

### Phase 2: AI Core (Weeks 3-5) 🧠
- [ ] llama.cpp JNI integration
- [ ] LlamaLLMProvider implementation
- [ ] Streaming response handling
- [ ] Basic chat UI with streaming
- [ ] Model loading/unloading

### Phase 3: Knowledge Base (Weeks 6-7) 📚
- [ ] Room database setup
- [ ] Embedding provider (MiniLM)
- [ ] sqlite-vec integration
- [ ] RAG pipeline
- [ ] Memory management

### Phase 3.5: Conversational Learning (Weeks 7-8) 🧠
- [ ] ConversationAnalyzer implementation
- [ ] Learning extraction prompts
- [ ] LearnedPreference storage
- [ ] PatternRecognizer for user patterns
- [ ] PersonalizationEngine for context enrichment
- [ ] Feedback/correction handling

### Phase 4: Voice (Weeks 9-10) 🗣️
- [ ] whisper.cpp integration
- [ ] Piper TTS integration
- [ ] Wake word detection
- [ ] Voice conversation flow

### Phase 5: System Integration (Weeks 10-11) ⚡
- [ ] Shizuku setup
- [ ] AccessibilityService
- [ ] NotificationListenerService
- [ ] Action execution engine
- [ ] Device collectors

### Phase 6: Personality & Avatar (Weeks 12-13) 👤
- [ ] Avatar rendering
- [ ] Expression engine
- [ ] Mood detection
- [ ] Relationship tracking
- [ ] Response styling

### Phase 7: Polish (Weeks 14-16) ✨
- [ ] Widgets
- [ ] Performance optimization
- [ ] Battery efficiency
- [ ] Testing + bug fixes
- [ ] Documentation

---

## 📊 Resource Estimates

| Component | Storage | RAM (Runtime) |
|-----------|---------|---------------|
| Phi-3.5-mini (Q4) | ~2.5 GB | ~2.0 GB |
| Whisper tiny.en | ~75 MB | ~150 MB |
| Piper TTS | ~50 MB | ~100 MB |
| Embeddings model | ~25 MB | ~50 MB |
| Avatar assets | ~200 MB | ~50 MB |
| App + DB | ~100 MB | ~200 MB |
| **Total** | **~3 GB** | **~2.5 GB** |

---

## 🎯 Key Principles

### 1. Interface-First Design
Define AI interfaces before implementations. This allows:
- Easy testing with mocks
- Swapping implementations without refactoring
- Clear contracts between layers

### 2. Streaming Everything
LLM responses MUST stream. Users see tokens appear in real-time, critical for perceived performance.

### 3. Graceful Degradation
If LLM fails, fall back to simpler responses. If STT fails, show keyboard. Always have a fallback.

### 4. Lazy Loading
Only load AI models when needed. Unload when idle. Memory is precious on mobile.

### 5. Aggressive Caching
Cache:
- Embeddings (don't re-compute)
- Frequent query results
- System state summaries
- LLM context windows

### 6. Continuous Learning from Conversations
JARVIS learns from every interaction:
- **Preference Extraction** - Analyze conversations to learn user preferences
- **Pattern Recognition** - Identify recurring questions, times, and contexts
- **Feedback Integration** - When user corrects JARVIS, store the correction
- **Proactive Suggestions** - Use patterns to anticipate user needs
- **Personalized Responses** - Apply learned preferences to improve responses

```
┌─────────────────────────────────────────────────────────────┐
│              CONVERSATIONAL LEARNING PIPELINE               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Conversation → ConversationAnalyzer                        │
│       │              │                                      │
│       │              ▼                                      │
│       │    Extract Key Information:                         │
│       │    - User preferences ("I prefer X over Y")        │
│       │    - Corrections ("No, I meant Z")                 │
│       │    - Patterns (morning greetings, evening reminders)│
│       │    - Facts (birthday, family names, work schedule)  │
│       │                                                     │
│       ▼                                                     │
│  LearnedPreferences ← PatternRecognizer                     │
│       │                                                     │
│       ▼                                                     │
│  PersonalizationEngine → Enhanced LLM Context               │
│       │                                                     │
│       ▼                                                     │
│  Next Response (personalized based on learnings)            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔗 Useful Resources

- [llama.cpp](https://github.com/ggerganov/llama.cpp) - LLM inference
- [whisper.cpp](https://github.com/ggerganov/whisper.cpp) - Speech recognition
- [Piper](https://github.com/rhasspy/piper) - Text-to-speech
- [Shizuku](https://github.com/RikkaApps/Shizuku) - System access
- [sqlite-vec](https://github.com/asg017/sqlite-vec) - Vector search
- [ONNX Runtime](https://onnxruntime.ai/) - ML inference
- [Jetpack Compose](https://developer.android.com/compose) - Modern UI
- [Koin](https://insert-koin.io/) - Dependency Injection

---

*Last updated: January 2026*
