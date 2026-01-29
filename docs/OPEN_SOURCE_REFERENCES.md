# 🔗 Open Source References for J.A.R.V.I.S.

> A curated list of open-source projects, libraries, and resources that can help accelerate J.A.R.V.I.S. development.

## 🎯 What We're Building

**JARVIS: An AI that learns from its mistakes and gets smarter every day.**

Unlike static AI assistants, JARVIS:
- 📈 **Improves accuracy** through every interaction
- 🧠 **Learns from corrections** - never makes the same mistake twice
- 🔄 **Accumulates knowledge** - the longer you use it, the better it gets
- 🎯 **Personalizes continuously** - adapts to your style and preferences

---

## 📊 Quick Reference Table

| Category | Project | Relevance | Stars |
|----------|---------|-----------|-------|
| **LLM Runtime** | llama.cpp | ⭐⭐⭐⭐⭐ | 75k+ |
| **Android LLM** | LLM Hub | ⭐⭐⭐⭐⭐ | - |
| **Android LLM** | Llamatik | ⭐⭐⭐⭐⭐ | - |
| **Desktop LLM** | Jan.ai | ⭐⭐⭐⭐ | 40k+ |
| **STT** | whisper.cpp | ⭐⭐⭐⭐⭐ | 37k+ |
| **TTS** | Piper | ⭐⭐⭐⭐⭐ | 7k+ |
| **Vector DB** | sqlite-vec | ⭐⭐⭐⭐⭐ | 5k+ |
| **Vector DB** | sqlite-vector | ⭐⭐⭐⭐⭐ | - |
| **System Access** | Shizuku | ⭐⭐⭐⭐⭐ | 10k+ |
| **Automation** | Shizuku-API | ⭐⭐⭐⭐⭐ | - |
| **UI Agent** | Panda (blurr) | ⭐⭐⭐⭐⭐ | 862+ |
| **UI Agent** | zerotap | ⭐⭐⭐⭐ | Closed |

---

## 🧠 LLM Inference

### 1. llama.cpp (Essential)
**The core of on-device LLM inference**

```
Repository: https://github.com/ggerganov/llama.cpp
License:    MIT
Language:   C/C++
```

| What It Provides | How It Helps JARVIS |
|------------------|---------------------|
| GGUF model loading | Load Phi-3, Gemma, Qwen models |
| Quantization support | Run 4-bit models on mobile |
| Android examples | Reference for JNI integration |
| Streaming inference | Real-time token generation |
| GPU acceleration | Vulkan/OpenCL support |

**Key Files to Study:**
- `examples/llama.android/` - Android app example
- `llama.h` - C API to wrap with JNI
- `examples/main/main.cpp` - Inference pipeline

**Android Integration:**
```kotlin
// JNI bridge pattern from llama.android example
external fun loadModel(modelPath: String, nCtx: Int): Long
external fun generate(contextPtr: Long, prompt: String, callback: (String) -> Boolean)
external fun freeModel(contextPtr: Long)
```

---

### 2. LLM Hub (Highly Relevant)
**Complete Android LLM app with RAG, TTS, and more**

```
Repository: https://github.com/timmyy123/LLM-Hub
License:    Open Source
Language:   Kotlin
```

| Feature | Relevance |
|---------|-----------|
| On-device chat with RAG | ⭐⭐⭐⭐⭐ Direct reference |
| TTS auto-readout | Voice output implementation |
| Multimodal input | Text, images, audio handling |
| Embedding integration | RAG memory system |
| 100% offline | Privacy-first architecture |

**What to Learn:**
- Chat UI with streaming responses
- RAG memory implementation
- Multi-modal input handling
- Native backend integration

---

### 3. Llamatik (Kotlin-First LLM Library)
**Production-ready Kotlin library for llama.cpp**

```
Website:    https://www.llamatik.com
License:    Open Source
Language:   Kotlin Multiplatform
```

| Feature | Benefit |
|---------|---------|
| Kotlin-first API | No JNI boilerplate |
| Compose UI examples | Reference implementation |
| GGUF support | All major models work |
| Streaming API | Built-in Flow support |
| Cross-platform | Future iOS expansion |

**Integration Example:**
```kotlin
// Llamatik provides clean Kotlin API
val llm = Llamatik.load("phi-3.5-mini.gguf")
llm.generate("Hello JARVIS").collect { token ->
    emit(token)  // Stream to UI
}
```

---

### 4. Jan.ai (Architecture Reference)
**Open-source ChatGPT alternative**

```
Repository: https://github.com/janhq/jan
License:    Open Source
Stars:      40k+
Language:   TypeScript/Tauri
```

| What to Learn | Application |
|---------------|-------------|
| Plugin architecture | Modular AI providers |
| Model management | Download/switch models |
| Conversation storage | Chat history patterns |
| Settings UI | Preference management |

---

### 5. Offline AI Chat (Android POC)
**Proof of concept for llama.cpp on Android**

```
Repository: https://github.com/weaktogeek/llama.cpp-android-java
License:    Open Source
Language:   Java
```

| Feature | Relevance |
|---------|-----------|
| JNI integration | llama.cpp bridge example |
| GGUF model loading | File handling patterns |
| Simple chat UI | RecyclerView implementation |
| 100% offline | No network dependencies |

---

## 🗣️ Speech-to-Text (STT)

### 1. whisper.cpp (Essential)
**Offline speech recognition**

```
Repository: https://github.com/ggerganov/whisper.cpp
License:    MIT
Language:   C/C++
```

| Model | Size | Speed | Quality |
|-------|------|-------|---------|
| tiny.en | 75MB | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| base.en | 142MB | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| small.en | 466MB | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

**Android Integration:**
```cpp
// whisper-jni.cpp pattern
JNIEXPORT jstring JNICALL
Java_com_jarvis_ai_whisper_WhisperJni_transcribe(
    JNIEnv *env, jobject thiz,
    jlong ctx, jfloatArray audio_data
) {
    // Convert audio and run inference
    whisper_full(ctx, params, pcm.data(), pcm.size());
    // Return transcription
}
```

**Key Files:**
- `examples/whisper.android/` - Android example
- `whisper.h` - C API
- `examples/stream/` - Real-time streaming

---

## 🔊 Text-to-Speech (TTS)

### 1. Piper (Essential)
**Fast, local neural TTS**

```
Repository: https://github.com/rhasspy/piper
License:    MIT
Language:   C++/ONNX
```

| Feature | Details |
|---------|---------|
| Voices | 30+ languages, 100+ voices |
| Quality | High/Medium/Low options |
| Speed | 10x faster than real-time |
| Format | ONNX models (~50MB each) |

**Voice Recommendations for JARVIS:**
```
British English (JARVIS style):
- en_GB-alan-medium.onnx (male, formal)
- en_GB-southern_english_male-medium.onnx

American English:
- en_US-lessac-medium.onnx (neutral)
- en_US-amy-medium.onnx (female)
```

**Integration with ONNX Runtime:**
```kotlin
class PiperTTSProvider(
    private val onnxSession: OrtSession
) : TTSProvider {

    override suspend fun synthesize(text: String): ByteArray {
        val phonemes = textToPhonemes(text)
        val inputs = createInputTensor(phonemes)
        val output = onnxSession.run(inputs)
        return output.toWavBytes()
    }
}
```

**Resources:**
- Voice samples: https://rhasspy.github.io/piper-samples/
- Voice list: https://github.com/rhasspy/piper/blob/master/VOICES.md

---

## 🗄️ Vector Database

### 1. sqlite-vec (Essential)
**Vector search as SQLite extension**

```
Repository: https://github.com/asg017/sqlite-vec
License:    MIT/Apache-2.0
Language:   C
```

| Feature | Benefit |
|---------|---------|
| Pure C | No external dependencies |
| SQLite extension | Works with Room |
| Float/Int8/Binary | Flexible vector types |
| vec0 virtual table | Easy querying |

**Usage with Room:**
```kotlin
// Load extension when database opens
database.openHelper.writableDatabase.apply {
    loadExtension("libvec0.so")
}

// Query similar memories
@Query("""
    SELECT m.*, v.distance
    FROM memories m
    JOIN vec_memories v ON m.id = v.rowid
    WHERE v.embedding MATCH :queryVector
    ORDER BY v.distance
    LIMIT :limit
""")
suspend fun findSimilar(queryVector: ByteArray, limit: Int): List<Memory>
```

---

### 2. sqlite-vector (Alternative)
**Another SQLite vector extension with Android support**

```
Repository: https://github.com/sqliteai/sqlite-vector
License:    Proprietary (free to use)
Language:   C
```

| Feature | Benefit |
|---------|---------|
| No virtual tables | Simpler integration |
| Android package | `implementation 'ai.sqlite:vector:0.9.34'` |
| SIMD optimized | Fast distance calculations |
| Quantization | Memory-efficient search |

**Gradle Integration:**
```kotlin
dependencies {
    implementation("ai.sqlite:vector:0.9.34")
}
```

---

## ⚡ System Integration

### 1. Shizuku (Essential)
**ADB-level access without root**

```
Repository: https://github.com/RikkaApps/Shizuku
License:    Apache-2.0
Language:   Java/Kotlin
```

| Capability | Use in JARVIS |
|------------|---------------|
| Shell commands | System control |
| Hidden APIs | Advanced features |
| Package management | App control |
| System settings | Device configuration |

**API Setup:**
```kotlin
dependencies {
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
}
```

---

### 2. Shizuku-API (Reference)
**Official Shizuku API with examples**

```
Repository: https://github.com/RikkaApps/Shizuku-API
License:    MIT
```

**Key Usage Pattern:**
```kotlin
class ShizukuService : IRemoteProcess.Stub() {

    fun executeCommand(command: String): CommandResult {
        if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
            return Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                .inputStream.bufferedReader().readText()
        }
        throw SecurityException("Shizuku permission not granted")
    }
}
```

---

### 3. ShizukuLegendaryStreamer (Utility)
**Simplified Shizuku wrapper**

```
Repository: https://github.com/Harshshah6/ShizukuLegendaryStreamer
License:    Open Source
Language:   Java
```

**Simple Command Execution:**
```java
legendaryStreamerShizuku.runCustomCommand(
    "settings put system screen_brightness 100",
    executionProcessListener
);
```

---

## 🤖 Complete AI Assistants (Reference)

### 1. risvn/voice-assistant (Full Stack Reference)
**Complete offline voice assistant pipeline**

```
Repository: https://github.com/risvn/voice-assistant
License:    Open Source
```

| Component | Implementation |
|-----------|----------------|
| STT | whisper.cpp |
| LLM | llama.cpp (TinyLlama) |
| TTS | Piper |

**Architecture Pattern:**
```
Microphone → Whisper → LLM → Piper → Speaker
```

**What to Learn:**
- End-to-end voice pipeline
- Model download scripts
- Binary integration
- Audio streaming

---

### 2. LocalRAG (RAG Reference)
**Complete local RAG system**

```
Repository: https://github.com/yanis112/LocalRAG
License:    Open Source
Language:   Python
```

| Feature | Application |
|---------|-------------|
| Document chunking | Memory ingestion |
| Chroma integration | Vector storage patterns |
| Evaluation pipeline | Quality testing |
| Multi-model support | Provider abstraction |

---

## 📱 UI Automation & AI Agents

### 1. Panda (blurr) ⭐ Essential
**Open-source Android AI Phone Operator**

```
Repository: https://github.com/Ayush0Chaudhary/blurr
Website:    https://heypanda.org
License:    Personal Use (Free for non-commercial)
Language:   Kotlin (99.1%)
Stars:      862+
```

| Feature | Relevance to JARVIS |
|---------|---------------------|
| AccessibilityService UI automation | ⭐⭐⭐⭐⭐ Direct code reference |
| Multi-agent architecture | Brain/Actuator separation pattern |
| Screen element parsing | UI understanding implementation |
| Touch gesture execution | Tap, swipe, type actions |
| Natural language commands | Intent → action mapping |
| Can be set as default Assistant | Google Assistant replacement |

**Architecture Pattern (Study This!):**
```
Eyes & Hands (Actuator)      The Brain (LLM)        The Agent (Operator)
      │                           │                        │
      ▼                           ▼                        ▼
AccessibilityService  →  LLM for reasoning  →  Executor with Notepad
(screen reading,         (decision making,      (task tracking,
 touch actions)          planning, analysis)    command execution)
```

**Key Learning Points:**
- Pure Kotlin implementation (no Java)
- AccessibilityService setup and permissions
- Screen element hierarchy parsing
- Multi-agent coordination pattern
- Voice assistant integration

**Integration Notes:**
```kotlin
// Panda uses Gemini API but the patterns apply to local LLMs
// Study these areas for JARVIS:
// - app/src/main/java/.../accessibility/ - Service implementation
// - app/src/main/java/.../agent/ - Agent coordination
// - app/src/main/java/.../ui/ - Compose UI patterns
```

**⚠️ License Note:**
Personal Use License - Free for personal/educational use.
Commercial use requires separate license from Panda AI.
For JARVIS: Great for learning patterns, but implement your own code.

---

### 2. zerotap (Behavior Reference)
**AI Agent App for Android Automation**

```
Platform:   Google Play Store
Type:       Closed Source (behavior reference only)
Developer:  Active development
```

| Feature | What to Learn |
|---------|---------------|
| Text/voice command control | UX patterns for commands |
| AccessibilityService automation | No root/ADB required approach |
| Multi-LLM support | Provider abstraction (Ollama, OpenRouter, OpenAI, Claude, Gemini, DeepSeek) |
| BYOK model | User brings their own API keys |
| On-device models (planned) | Gemma 3n, Qwen - confirms viability |

**Why Reference zerotap:**
- Validates that our AccessibilityService approach works
- Shows that on-device models for UI automation is feasible
- Developer quote: *"Hopefully one day we will have good and reliable on-device LLM"* - JARVIS aims to be this
- UX patterns for voice + text command interface

**Key Insights from zerotap:**
1. AccessibilityService is sufficient for full phone control
2. No root or ADB required for most automation tasks
3. Multiple LLM providers can be abstracted behind common interface
4. Voice activation as primary input method works well
5. On-device models are the next frontier (aligns with JARVIS goals)

**Competitive Advantage for JARVIS:**
While zerotap relies on cloud APIs, JARVIS will be 100% offline -
this is the key differentiator and privacy advantage.

---

## 📚 Research & Papers

### Mobile LLM Resources
```
Repository: https://github.com/stevelaskaridis/awesome-mobile-llm
```

| Paper | Key Insight |
|-------|-------------|
| MobileLLM (Meta) | Sub-billion parameter optimization |
| PowerInfer-2 | Fast inference on smartphones |
| MobileQuant | Mobile-friendly quantization |
| PalmBench | Mobile LLM benchmarking |

---

## 🛠️ Development Tools

### Android LLM Tutorial
```
Repository: https://github.com/JackZeng0208/llama.cpp-android-tutorial
```

**Covers:**
- Building llama.cpp for Android
- Adreno OpenCL GPU acceleration
- JNI bridging patterns
- Performance optimization

---

## 📋 Integration Checklist

### Phase 1: Core AI
- [ ] Clone llama.cpp, build for Android
- [ ] Study LLM Hub for Kotlin patterns
- [ ] Integrate Llamatik or build custom JNI
- [ ] Test with Phi-3.5-mini model

### Phase 2: Voice
- [ ] Clone whisper.cpp, build for Android
- [ ] Download Piper voices
- [ ] Implement voice pipeline
- [ ] Test end-to-end voice chat

### Phase 3: Knowledge
- [ ] Integrate sqlite-vec or sqlite-vector
- [ ] Study LocalRAG for chunking patterns
- [ ] Implement embedding pipeline
- [ ] Build RAG retrieval

### Phase 4: System
- [ ] Integrate Shizuku
- [ ] Build AccessibilityService
- [ ] Implement device collectors
- [ ] Test system actions

---

## 🔗 Quick Links

| Resource | URL |
|----------|-----|
| llama.cpp | https://github.com/ggerganov/llama.cpp |
| whisper.cpp | https://github.com/ggerganov/whisper.cpp |
| Piper TTS | https://github.com/rhasspy/piper |
| Piper Samples | https://rhasspy.github.io/piper-samples/ |
| sqlite-vec | https://github.com/asg017/sqlite-vec |
| sqlite-vector | https://github.com/sqliteai/sqlite-vector |
| Shizuku | https://github.com/RikkaApps/Shizuku |
| Shizuku-API | https://github.com/RikkaApps/Shizuku-API |
| LLM Hub | https://github.com/timmyy123/LLM-Hub |
| Llamatik | https://www.llamatik.com |
| Jan.ai | https://github.com/janhq/jan |
| **Panda (blurr)** | https://github.com/Ayush0Chaudhary/blurr |
| **zerotap** | https://play.google.com/store/apps/details?id=... |
| Awesome Mobile LLM | https://github.com/stevelaskaridis/awesome-mobile-llm |
| GGUF Models | https://huggingface.co/models?library=gguf |

---

## 💡 Key Takeaways

1. **LLM Hub is your closest reference** - It's a Kotlin Android app with RAG, TTS, and on-device LLM. Study its architecture.

2. **Llamatik simplifies Kotlin integration** - Consider using it instead of raw JNI.

3. **sqlite-vector has official Android support** - Easier than sqlite-vec for RAG.

4. **Piper voices are production-ready** - Use British male for authentic JARVIS feel.

5. **Shizuku-API has excellent documentation** - Follow their demo project structure.

6. **whisper.cpp tiny.en is optimal** - Best speed/accuracy tradeoff for mobile.

7. **Panda (blurr) is THE AccessibilityService reference** - Pure Kotlin implementation of UI automation with multi-agent architecture. Study this for device control.

8. **zerotap validates the approach** - Production app proving AI + AccessibilityService works. JARVIS advantage: 100% offline.

---

*Last updated: January 2026*
