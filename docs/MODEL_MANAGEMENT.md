# 🤖 On-Device AI Model Management Guide

> **Complete guide for selecting, downloading, storing, and managing AI models for JARVIS**

This document addresses all concerns about on-device AI model management, including:
- ✅ Which models to use (with exact versions)
- ✅ Model formats and their benefits
- ✅ Where to download from (with direct links)
- ✅ Storage architecture for easy model swapping
- ✅ Memory management strategies
- ✅ Size optimization techniques
- ✅ Step-by-step setup instructions

---

## 📋 Table of Contents

1. [Model Selection Philosophy](#model-selection-philosophy)
2. [Recommended Models](#recommended-models)
3. [Model Formats Explained](#model-formats-explained)
4. [Storage Architecture](#storage-architecture)
5. [Memory Management](#memory-management)
6. [Download Instructions](#download-instructions)
7. [Model Swapping Guide](#model-swapping-guide)
8. [Size Comparison Tables](#size-comparison-tables)
9. [Troubleshooting](#troubleshooting)

---

## 🎯 Model Selection Philosophy

### Core Principles for On-Device AI

```
┌─────────────────────────────────────────────────────────────┐
│              ON-DEVICE AI MODEL CRITERIA                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Size:        < 3GB per model (manageable)               │
│  2. Format:      GGUF/ONNX (optimized for mobile)           │
│  3. Quantization: 4-bit minimum (balance size/quality)      │
│  4. Performance:  < 2 seconds per response                   │
│  5. License:     MIT/Apache-2.0 (fully open)                │
│  6. Support:     Active community and updates               │
│  7. Swappable:   Easy to replace without code changes       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Why These Models?

| Criterion | Reasoning |
|-----------|-----------|
| **Manageable Size** | Models < 3GB fit on most devices with 6GB+ RAM |
| **Separate Storage** | External storage allows easy swapping without app reinstall |
| **4-bit Quantization** | Best balance: 80% of full precision quality at 25% size |
| **GGUF Format** | Optimized for CPU/GPU inference, widely supported |
| **Open License** | MIT/Apache-2.0 ensures commercial use and modification |

---

## 🏆 Recommended Models

### 1. Large Language Model (LLM)

#### Primary Choice: **Phi-3.5-mini-instruct**

```yaml
Model Details:
  Name: Phi-3.5-mini-instruct
  Developer: Microsoft
  License: MIT
  Parameters: 3.8 billion
  Context Length: 128K tokens
  Quantization: Q4_K_M (4-bit)
  
Size Information:
  Uncompressed: ~7.6 GB
  Quantized (Q4_K_M): ~2.5 GB
  In-Memory (Runtime): ~3.2 GB
  
Performance:
  First Token: ~800ms
  Tokens/Second: 8-12 tokens/s (Snapdragon 8 Gen 2)
  Total Response (50 tokens): ~4-6 seconds
  
Format: GGUF
File Name: Phi-3.5-mini-instruct-Q4_K_M.gguf
```

**Why Phi-3.5-mini?**
- ✅ Best reasoning capability for its size
- ✅ Instruction-tuned for assistant tasks
- ✅ 128K context window (remembers long conversations)
- ✅ MIT license (fully open)
- ✅ Optimized for on-device inference
- ✅ Microsoft support and updates

#### Alternative Options:

| Model | Size | Pros | Cons | Use When |
|-------|------|------|------|----------|
| **Gemma-2-2B** | 1.5 GB | Smaller, faster | Less capable reasoning | Limited device storage |
| **Qwen2.5-3B** | 2.1 GB | Multilingual, good quality | Slightly slower | Need multiple languages |
| **Llama-3.2-3B** | 2.0 GB | Strong general performance | Not as instruction-tuned | General tasks |
| **TinyLlama-1.1B** | 0.6 GB | Very small, very fast | Limited capabilities | Ultra-low-end devices |

---

### 2. Speech-to-Text (STT)

#### Primary Choice: **Whisper Tiny English**

```yaml
Model Details:
  Name: Whisper Tiny English
  Developer: OpenAI
  License: MIT
  Parameters: 39 million
  Language: English only
  
Size Information:
  Model Size: 75 MB
  In-Memory: ~150 MB
  
Performance:
  Processing: Real-time (1x speed)
  Latency: ~200-500ms for 5 second audio
  Accuracy: 85-90% for clear speech
  
Format: GGML (for whisper.cpp)
File Name: ggml-tiny.en.bin
```

**Why Whisper Tiny?**
- ✅ Real-time transcription
- ✅ Minimal storage footprint
- ✅ Good accuracy for clear speech
- ✅ MIT license
- ✅ Works offline

#### Alternative Options:

| Model | Size | Pros | Cons | Use When |
|-------|------|------|------|----------|
| **Whisper Base** | 142 MB | Better accuracy | 2x slower | Need higher accuracy |
| **Whisper Small** | 466 MB | Much better accuracy | 4x slower | Accuracy critical |
| **Whisper Medium** | 1.5 GB | Near-perfect accuracy | 8x slower | Have powerful device |

---

### 3. Text-to-Speech (TTS)

#### Primary Choice: **Piper English GB Alan**

```yaml
Model Details:
  Name: Piper English GB Alan (Medium Quality)
  Developer: Rhasspy Project
  License: MIT
  Voice: British Male
  Quality: Medium (good balance)
  
Size Information:
  Model: 50 MB (.onnx file)
  Config: 5 KB (.json file)
  Total: 55 MB
  
Performance:
  Speed: Real-time (1x)
  Latency: ~100-300ms for short phrases
  Quality: Natural, clear pronunciation
  
Format: ONNX
Files: 
  - en_GB-alan-medium.onnx
  - en_GB-alan-medium.onnx.json
```

**Why Piper Alan?**
- ✅ Natural British accent (JARVIS-like)
- ✅ Very small size
- ✅ Real-time generation
- ✅ MIT license
- ✅ ONNX format (cross-platform)

#### Alternative Options:

| Voice | Size | Accent | Quality | Use When |
|-------|------|--------|---------|----------|
| **Piper US Amy** | 50 MB | American Female | Medium | Prefer US accent |
| **Piper US Ryan** | 50 MB | American Male | Medium | US male voice |
| **Piper GB Jenny** | 50 MB | British Female | Medium | Female voice |
| **Piper GB Alan Low** | 20 MB | British Male | Low | Save storage |
| **Piper GB Alan High** | 150 MB | British Male | High | Best quality |

---

### 4. Embedding Model

#### Primary Choice: **all-MiniLM-L6-v2**

```yaml
Model Details:
  Name: all-MiniLM-L6-v2
  Developer: Microsoft (Sentence Transformers)
  License: Apache-2.0
  Dimensions: 384
  
Size Information:
  Model Size: 25 MB
  In-Memory: ~50 MB
  
Performance:
  Encoding Speed: ~1000 sentences/second
  Vector Size: 384 floats (1.5 KB per embedding)
  
Format: ONNX
File Name: all-MiniLM-L6-v2.onnx
```

**Why all-MiniLM-L6-v2?**
- ✅ Excellent semantic understanding
- ✅ Very small and fast
- ✅ Perfect for RAG (Retrieval-Augmented Generation)
- ✅ Apache-2.0 license
- ✅ Widely used and tested

---

## 📦 Model Formats Explained

### GGUF Format (LLM)

```
GGUF = GPT-Generated Unified Format
```

**What is it?**
- Binary format optimized for LLM inference
- Successor to GGML format
- Includes model metadata, tokenizer, and weights in one file

**Why use GGUF?**
- ✅ Single-file distribution (easy to manage)
- ✅ Fast loading and inference
- ✅ Supports quantization (4-bit, 5-bit, 8-bit)
- ✅ Compatible with llama.cpp (Android ready)
- ✅ Memory-mapped file access (efficient)

**Quantization Types:**

| Quantization | Size | Quality | Use Case |
|--------------|------|---------|----------|
| **Q4_K_M** | 25% of original | ~95% quality | **RECOMMENDED** - Best balance |
| **Q4_K_S** | 22% of original | ~92% quality | When storage critical |
| **Q5_K_M** | 30% of original | ~97% quality | When quality critical |
| **Q8_0** | 50% of original | ~99% quality | High-end devices only |

**File Naming Convention:**
```
<model-name>-<quantization>.gguf

Examples:
- Phi-3.5-mini-instruct-Q4_K_M.gguf  ← 4-bit medium quality
- Phi-3.5-mini-instruct-Q5_K_M.gguf  ← 5-bit medium quality
- Gemma-2-2b-it-Q4_K_M.gguf          ← Gemma 2B quantized
```

---

### ONNX Format (TTS, Embeddings)

```
ONNX = Open Neural Network Exchange
```

**What is it?**
- Open standard for ML models
- Cross-platform and framework-agnostic
- Optimized for inference

**Why use ONNX?**
- ✅ Works on CPU, GPU, and NPU
- ✅ Optimized inference speed
- ✅ Cross-platform (Android, iOS, desktop)
- ✅ Industry standard
- ✅ Smaller than PyTorch/TensorFlow formats

**Components:**
```
model.onnx         ← Neural network weights and graph
model.onnx.json    ← Configuration (sample rate, symbols, etc.)
```

---

### GGML Format (Whisper STT)

```
GGML = Georgi Gerganov Machine Learning
```

**What is it?**
- Predecessor to GGUF
- Optimized for Whisper models
- Used by whisper.cpp

**Why use GGML for Whisper?**
- ✅ Whisper.cpp native format
- ✅ Fast audio transcription
- ✅ Quantized for mobile
- ✅ Proven on Android

**File Naming:**
```
ggml-<model-size>.<language>.bin

Examples:
- ggml-tiny.en.bin    ← Tiny model, English only
- ggml-base.en.bin    ← Base model, English only
- ggml-small.bin      ← Small model, multilingual
```

---

## 🗂️ Storage Architecture

### Directory Structure

```
/storage/emulated/0/Android/data/com.jarvis.ai/files/
├── models/
│   ├── llm/
│   │   ├── active/                          # Currently active LLM
│   │   │   └── Phi-3.5-mini-instruct-Q4_K_M.gguf (2.5 GB)
│   │   ├── backup/                          # Backup/alternative models
│   │   │   ├── Gemma-2-2b-it-Q4_K_M.gguf (1.5 GB)
│   │   │   └── Qwen2.5-3B-Instruct-Q4_K_M.gguf (2.1 GB)
│   │   └── metadata.json                    # Model info and config
│   │
│   ├── stt/
│   │   ├── active/
│   │   │   └── ggml-tiny.en.bin (75 MB)
│   │   ├── backup/
│   │   │   └── ggml-base.en.bin (142 MB)
│   │   └── metadata.json
│   │
│   ├── tts/
│   │   ├── active/
│   │   │   ├── en_GB-alan-medium.onnx (50 MB)
│   │   │   └── en_GB-alan-medium.onnx.json (5 KB)
│   │   ├── backup/
│   │   │   ├── en_US-amy-medium.onnx (50 MB)
│   │   │   └── en_US-amy-medium.onnx.json (5 KB)
│   │   └── metadata.json
│   │
│   └── embeddings/
│       ├── active/
│       │   └── all-MiniLM-L6-v2.onnx (25 MB)
│       └── metadata.json
│
└── cache/                                   # Temporary runtime data
    ├── llm_cache/                           # KV cache for LLM
    └── stt_cache/                           # Audio processing cache
```

### metadata.json Structure

```json
{
  "model_type": "llm",
  "active_model": {
    "name": "Phi-3.5-mini-instruct",
    "version": "Q4_K_M",
    "file": "Phi-3.5-mini-instruct-Q4_K_M.gguf",
    "size_bytes": 2621440000,
    "format": "GGUF",
    "quantization": "Q4_K_M",
    "context_length": 131072,
    "parameters": "3.8B",
    "license": "MIT",
    "checksum": "sha256:abc123...",
    "last_used": "2026-01-30T12:00:00Z",
    "performance_stats": {
      "avg_tokens_per_second": 10.5,
      "avg_first_token_ms": 800,
      "device": "Snapdragon 8 Gen 2"
    }
  },
  "available_models": [
    {
      "name": "Gemma-2-2B-IT",
      "file": "Gemma-2-2b-it-Q4_K_M.gguf",
      "size_bytes": 1572864000,
      "location": "backup",
      "status": "ready"
    }
  ]
}
```

### Benefits of This Architecture

| Benefit | Description |
|---------|-------------|
| **Easy Swapping** | Just move files between `active/` and `backup/` folders |
| **No Code Changes** | App reads from `active/` folder, model is transparent |
| **Multiple Models** | Store alternatives in `backup/` without impacting storage |
| **Metadata Tracking** | JSON files track performance and configuration |
| **External Storage** | Models on external storage, not in APK |
| **User Control** | Users can manually add/remove models |

---

## 💾 Memory Management

### Memory Budget Planning

```
┌─────────────────────────────────────────────────────────────┐
│              MEMORY USAGE BREAKDOWN (6GB Device)            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Android System:           1.5 GB  (25%)                    │
│  Other Apps (Background):  1.0 GB  (17%)                    │
│  JARVIS App:               3.5 GB  (58%)                    │
│    ├─ LLM (Phi-3.5):      2.8 GB  (47%)                    │
│    ├─ STT (Whisper):      0.2 GB  (3%)                     │
│    ├─ TTS (Piper):        0.1 GB  (2%)                     │
│    ├─ Embeddings:         0.1 GB  (2%)                     │
│    ├─ App UI:             0.2 GB  (3%)                     │
│    └─ Cache/Buffers:      0.1 GB  (2%)                     │
│                                                             │
│  Total Used:               4.5 GB  (75%)                    │
│  Available:                1.5 GB  (25%) ← Safety buffer   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Optimization Strategies

#### 1. Lazy Loading

```kotlin
// Don't load all models at startup
class JarvisAIService {
    private var llmModel: LlamaModel? = null
    private var sttModel: WhisperModel? = null
    private var ttsModel: PiperModel? = null
    
    // Load only when needed
    suspend fun chat(message: String): String {
        if (llmModel == null) {
            llmModel = loadLLM()  // Load on first use
        }
        return llmModel.generate(message)
    }
    
    suspend fun transcribe(audio: ByteArray): String {
        if (sttModel == null) {
            sttModel = loadSTT()  // Load on first use
        }
        return sttModel.transcribe(audio)
    }
}
```

#### 2. Model Unloading

```kotlin
// Unload models when not in use
class ModelMemoryManager {
    private val inactivityTimeout = 5.minutes
    
    fun scheduleUnload(model: AIModel) {
        scope.launch {
            delay(inactivityTimeout)
            if (!model.isActive()) {
                model.unload()  // Free memory
                log("Unloaded ${model.name} to save memory")
            }
        }
    }
}
```

#### 3. Context Window Management

```kotlin
// Keep only recent conversation history
class ContextManager {
    private val maxTokens = 8000  // ~6-8 messages
    
    fun trimContext(messages: List<Message>): List<Message> {
        var tokens = 0
        return messages.reversed()
            .takeWhile { 
                tokens += it.tokenCount
                tokens <= maxTokens 
            }
            .reversed()
    }
}
```

#### 4. KV Cache Optimization

```kotlin
// Reuse KV cache for continued conversations
class LlamaInference {
    private var kvCache: KVCache? = null
    
    fun generate(prompt: String, reuseCache: Boolean = true): String {
        if (!reuseCache || kvCache == null) {
            kvCache = KVCache.create()
        }
        return llamaGenerate(prompt, kvCache)
    }
}
```

### Memory Monitoring

```kotlin
class MemoryMonitor {
    fun checkMemoryHealth(): MemoryStatus {
        val runtime = Runtime.getRuntime()
        val usedMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576
        val maxMB = runtime.maxMemory() / 1048576
        val freeMB = maxMB - usedMB
        val percentUsed = (usedMB * 100) / maxMB
        
        return when {
            percentUsed < 70 -> MemoryStatus.HEALTHY
            percentUsed < 85 -> MemoryStatus.WARNING  // Start unloading
            else -> MemoryStatus.CRITICAL  // Unload immediately
        }
    }
}
```

---

## 📥 Download Instructions

### Method 1: Direct Download (Recommended)

#### Step 1: Download LLM (Phi-3.5-mini)

```bash
# Open browser or use wget/curl

# Source: https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf
# File: Phi-3.5-mini-instruct-Q4_K_M.gguf
# Size: 2.5 GB

# Direct link:
https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf

# Using wget:
wget https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf \
  -O Phi-3.5-mini-instruct-Q4_K_M.gguf

# Using curl:
curl -L https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf \
  -o Phi-3.5-mini-instruct-Q4_K_M.gguf
```

#### Step 2: Download STT (Whisper Tiny)

```bash
# Source: https://huggingface.co/ggerganov/whisper.cpp
# File: ggml-tiny.en.bin
# Size: 75 MB

# Direct link:
https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en.bin

# Using wget:
wget https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en.bin \
  -O ggml-tiny.en.bin

# Using curl:
curl -L https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en.bin \
  -o ggml-tiny.en.bin
```

#### Step 3: Download TTS (Piper Alan)

```bash
# Source: https://github.com/rhasspy/piper/releases
# Files: en_GB-alan-medium.onnx + .onnx.json
# Size: 50 MB + 5 KB

# Model file:
https://github.com/rhasspy/piper/releases/download/v1.2.0/en_GB-alan-medium.onnx

# Config file:
https://github.com/rhasspy/piper/releases/download/v1.2.0/en_GB-alan-medium.onnx.json

# Using wget:
wget https://github.com/rhasspy/piper/releases/download/v1.2.0/en_GB-alan-medium.onnx
wget https://github.com/rhasspy/piper/releases/download/v1.2.0/en_GB-alan-medium.onnx.json

# Using curl:
curl -L https://github.com/rhasspy/piper/releases/download/v1.2.0/en_GB-alan-medium.onnx -o en_GB-alan-medium.onnx
curl -L https://github.com/rhasspy/piper/releases/download/v1.2.0/en_GB-alan-medium.onnx.json -o en_GB-alan-medium.onnx.json
```

#### Step 4: Download Embeddings (all-MiniLM-L6-v2)

```bash
# Source: https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2
# File: model.onnx (rename to all-MiniLM-L6-v2.onnx)
# Size: 25 MB

# Direct link:
https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx

# Using wget:
wget https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx \
  -O all-MiniLM-L6-v2.onnx

# Using curl:
curl -L https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx \
  -o all-MiniLM-L6-v2.onnx
```

---

### Method 2: Using Hugging Face CLI (For Developers)

```bash
# Install Hugging Face CLI
pip install huggingface-hub

# Download with huggingface-cli
huggingface-cli download microsoft/Phi-3.5-mini-instruct-gguf \
  Phi-3.5-mini-instruct-Q4_K_M.gguf \
  --local-dir ./models/llm

huggingface-cli download ggerganov/whisper.cpp \
  ggml-tiny.en.bin \
  --local-dir ./models/stt

huggingface-cli download sentence-transformers/all-MiniLM-L6-v2 \
  onnx/model.onnx \
  --local-dir ./models/embeddings
```

---

### Method 3: In-App Download (Future Feature)

```kotlin
// Future: Download models from within JARVIS app
class ModelDownloader {
    suspend fun downloadModel(
        modelType: ModelType,
        modelName: String,
        onProgress: (Float) -> Unit
    ) {
        val url = getModelUrl(modelType, modelName)
        val destination = getModelPath(modelType, modelName)
        
        // Download with progress
        downloadFile(url, destination, onProgress)
        
        // Verify checksum
        verifyModel(destination)
        
        // Update metadata
        updateMetadata(modelType, modelName)
    }
}
```

---

## 🔄 Model Swapping Guide

### Swapping LLM Models

#### Option 1: Using App Settings (When Implemented)

```
Settings → AI Models → Language Model
├─ Active: Phi-3.5-mini-instruct (2.5 GB)
├─ Available Models:
│   ├─ ○ Gemma-2-2B-IT (1.5 GB) [Faster, Less Capable]
│   ├─ ○ Qwen2.5-3B-Instruct (2.1 GB) [Multilingual]
│   └─ ○ Llama-3.2-3B-Instruct (2.0 GB) [Balanced]
└─ [+ Download New Model]

Tap model → "Set as Active" → App reloads → Done!
```

#### Option 2: Manual File Swap

```bash
# On Android device or via ADB

# 1. Navigate to models directory
cd /storage/emulated/0/Android/data/com.jarvis.ai/files/models/llm/

# 2. Move current active model to backup
mv active/Phi-3.5-mini-instruct-Q4_K_M.gguf backup/

# 3. Move desired model to active
mv backup/Gemma-2-2b-it-Q4_K_M.gguf active/

# 4. Restart JARVIS app
# App automatically detects new model in active/ folder
```

#### Option 3: Via Code (Developer)

```kotlin
class ModelSwapper {
    suspend fun swapLLM(newModelName: String) {
        // 1. Unload current model
        aiService.unloadLLM()
        
        // 2. Move files
        val activeDir = File(modelsDir, "llm/active")
        val backupDir = File(modelsDir, "llm/backup")
        
        // Move current to backup
        activeDir.listFiles()?.forEach { file ->
            file.renameTo(File(backupDir, file.name))
        }
        
        // Move new to active
        val newModel = File(backupDir, newModelName)
        newModel.renameTo(File(activeDir, newModelName))
        
        // 3. Load new model
        aiService.loadLLM()
        
        // 4. Update metadata
        updateMetadata("llm", newModelName)
    }
}
```

### Why This Works

- ✅ **No Code Changes**: App always reads from `active/` folder
- ✅ **No Recompilation**: Models are external to APK
- ✅ **No App Update**: Users can swap models independently
- ✅ **Fast**: Just file operations, no downloads needed
- ✅ **Reversible**: Easy to switch back

---

## 📊 Size Comparison Tables

### Total Storage Requirements

| Configuration | LLM | STT | TTS | Embeddings | Total | RAM Required |
|---------------|-----|-----|-----|------------|-------|--------------|
| **Minimum (Low-End)** | TinyLlama 1.1B (0.6 GB) | Whisper Tiny (75 MB) | Piper Low (20 MB) | MiniLM (25 MB) | **0.7 GB** | 4 GB |
| **Recommended (Balanced)** | Phi-3.5-mini (2.5 GB) | Whisper Tiny (75 MB) | Piper Medium (50 MB) | MiniLM (25 MB) | **2.7 GB** | 6 GB |
| **High-End (Best Quality)** | Phi-3.5-mini Q5 (3.2 GB) | Whisper Base (142 MB) | Piper High (150 MB) | MiniLM (25 MB) | **3.5 GB** | 8 GB |
| **Ultra (Maximum)** | Qwen2.5-7B (4.5 GB) | Whisper Small (466 MB) | Piper High (150 MB) | MiniLM (25 MB) | **5.1 GB** | 10 GB |

### LLM Model Comparison

| Model | Size (Q4_K_M) | Parameters | Context | Speed | Quality | Best For |
|-------|---------------|------------|---------|-------|---------|----------|
| TinyLlama-1.1B | 0.6 GB | 1.1B | 2K | ⚡⚡⚡⚡⚡ | ⭐⭐ | Ultra low-end devices |
| Gemma-2-2B | 1.5 GB | 2B | 8K | ⚡⚡⚡⚡ | ⭐⭐⭐ | Low-end devices, fast responses |
| Llama-3.2-3B | 2.0 GB | 3B | 128K | ⚡⚡⚡ | ⭐⭐⭐⭐ | Balanced, long context |
| Qwen2.5-3B | 2.1 GB | 3B | 32K | ⚡⚡⚡ | ⭐⭐⭐⭐ | Multilingual support |
| **Phi-3.5-mini** | **2.5 GB** | **3.8B** | **128K** | **⚡⚡⚡** | **⭐⭐⭐⭐⭐** | **Best reasoning for size** |
| Qwen2.5-7B | 4.5 GB | 7B | 128K | ⚡⚡ | ⭐⭐⭐⭐⭐ | High-end devices only |

### Quantization Impact

| Quantization | Size vs FP16 | Quality Loss | Inference Speed | Recommended |
|--------------|--------------|--------------|-----------------|-------------|
| Q4_K_S | 22% | ~8% | Fastest | Storage critical |
| **Q4_K_M** | **25%** | **~5%** | **Very Fast** | **✅ RECOMMENDED** |
| Q5_K_M | 30% | ~3% | Fast | Quality critical |
| Q6_K | 38% | ~2% | Moderate | High-end only |
| Q8_0 | 50% | ~1% | Slower | Rarely needed |

### STT Model Comparison

| Model | Size | Speed | Accuracy | Languages | Best For |
|-------|------|-------|----------|-----------|----------|
| **Whisper Tiny** | **75 MB** | **Real-time** | **85-90%** | **EN only** | **✅ Default** |
| Whisper Base | 142 MB | 2x slower | 90-93% | Multilingual | Better accuracy |
| Whisper Small | 466 MB | 4x slower | 93-95% | Multilingual | Quality critical |
| Whisper Medium | 1.5 GB | 8x slower | 95-97% | Multilingual | High-end only |

### TTS Model Comparison

| Voice | Size | Quality | Speed | Accent | Best For |
|-------|------|---------|-------|--------|----------|
| Piper GB Alan Low | 20 MB | Fair | Very Fast | British Male | Storage critical |
| **Piper GB Alan Med** | **50 MB** | **Good** | **Fast** | **British Male** | **✅ Default** |
| Piper GB Alan High | 150 MB | Excellent | Moderate | British Male | Quality critical |
| Piper US Amy Med | 50 MB | Good | Fast | American Female | US preference |
| Piper US Ryan Med | 50 MB | Good | Fast | American Male | US male voice |

---

## 🔧 Troubleshooting

### Issue 1: Out of Memory Error

**Symptoms:**
```
E/JARVIS: OutOfMemoryError: Failed to allocate XXX MB
```

**Solutions:**

1. **Switch to smaller LLM**
```bash
# Use Gemma-2-2B instead of Phi-3.5-mini
Saves: 1 GB RAM
```

2. **Reduce context window**
```kotlin
// In model config
contextSize = 4096  // Instead of 8192
```

3. **Enable aggressive unloading**
```kotlin
// Unload models after 2 minutes idle
memoryManager.inactivityTimeout = 2.minutes
```

---

### Issue 2: Slow Inference

**Symptoms:**
- First token takes > 2 seconds
- Response generation > 10 seconds

**Solutions:**

1. **Check quantization**
```bash
# Ensure using Q4_K_M, not Q8 or higher
# Q4_K_M is 2-3x faster than Q8
```

2. **Reduce context length**
```kotlin
// Keep only recent messages
maxContextTokens = 4000  // ~4-6 messages
```

3. **Enable GPU acceleration (if available)**
```kotlin
llamaConfig.useGPU = true
llamaConfig.gpuLayers = 32  // Offload to GPU
```

---

### Issue 3: Model Not Found

**Symptoms:**
```
E/JARVIS: Model file not found: /path/to/model.gguf
```

**Solutions:**

1. **Verify file location**
```bash
# Check active directory
ls -lh /storage/emulated/0/Android/data/com.jarvis.ai/files/models/llm/active/
```

2. **Check file permissions**
```bash
# Ensure app has read permissions
chmod 644 model.gguf
```

3. **Verify file integrity**
```bash
# Check file size matches expected
# Phi-3.5-mini Q4_K_M should be ~2.5 GB
ls -lh model.gguf
```

---

### Issue 4: Corrupted Model Download

**Symptoms:**
- App crashes on model load
- "Invalid magic number" error

**Solutions:**

1. **Verify checksum**
```bash
# Download checksum from Hugging Face
sha256sum Phi-3.5-mini-instruct-Q4_K_M.gguf

# Compare with official checksum
```

2. **Re-download model**
```bash
# Delete and download again
rm model.gguf
wget [url] -O model.gguf
```

3. **Try different download method**
```bash
# If wget failed, try curl or browser download
curl -L [url] -o model.gguf
```

---

### Issue 5: Model Version Mismatch

**Symptoms:**
```
E/JARVIS: Incompatible model version
```

**Solutions:**

1. **Check llama.cpp version**
```kotlin
// Ensure llama.cpp library matches model format
// GGUF requires llama.cpp >= v0.0.1475
```

2. **Update native libraries**
```bash
# Rebuild native modules
./gradlew :ai:ai-llama:clean :ai:ai-llama:build
```

3. **Use compatible model version**
```bash
# Download latest model version from Hugging Face
# Check model card for compatibility notes
```

---

## 📚 Additional Resources

### Official Documentation

| Resource | URL | Purpose |
|----------|-----|---------|
| **llama.cpp** | https://github.com/ggerganov/llama.cpp | LLM inference engine |
| **whisper.cpp** | https://github.com/ggerganov/whisper.cpp | Speech-to-text |
| **Piper TTS** | https://github.com/rhasspy/piper | Text-to-speech |
| **ONNX Runtime** | https://onnxruntime.ai/ | ONNX model runtime |
| **Hugging Face** | https://huggingface.co/ | Model repository |

### Model Repositories

| Models | URL | Content |
|--------|-----|---------|
| **Phi-3.5 GGUF** | https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf | LLM models |
| **Whisper Models** | https://huggingface.co/ggerganov/whisper.cpp | STT models |
| **Piper Voices** | https://github.com/rhasspy/piper/releases | TTS voices |
| **Sentence Transformers** | https://huggingface.co/sentence-transformers | Embedding models |

### Community & Support

| Platform | URL | Purpose |
|----------|-----|---------|
| **llama.cpp Discord** | https://discord.gg/llamacpp | Technical support |
| **Hugging Face Forums** | https://discuss.huggingface.co/ | Model discussions |
| **r/LocalLLaMA** | https://reddit.com/r/LocalLLaMA | Community tips |

---

## ✅ Quick Checklist

### Before Starting Development

- [ ] Read this entire document
- [ ] Understand model selection criteria
- [ ] Know which quantization to use (Q4_K_M for most)
- [ ] Understand storage architecture
- [ ] Review memory management strategies

### During Setup

- [ ] Download Phi-3.5-mini-instruct-Q4_K_M.gguf (2.5 GB)
- [ ] Download ggml-tiny.en.bin (75 MB)
- [ ] Download en_GB-alan-medium.onnx + .json (55 MB)
- [ ] Download all-MiniLM-L6-v2.onnx (25 MB)
- [ ] Verify all file sizes match expected values
- [ ] Place models in correct directory structure
- [ ] Create metadata.json files
- [ ] Test model loading in app

### Testing

- [ ] Verify LLM loads without OOM
- [ ] Test inference speed (< 2s first token)
- [ ] Check STT accuracy (85%+ for clear speech)
- [ ] Verify TTS sounds natural
- [ ] Test model swapping functionality
- [ ] Monitor memory usage during operation
- [ ] Test on minimum spec device (6GB RAM)

---

## 📞 Getting Help

If you encounter issues not covered in this guide:

1. **Check logs**: `adb logcat | grep JARVIS`
2. **Verify setup**: Follow troubleshooting section
3. **Check versions**: Ensure all components are compatible
4. **Ask community**: llama.cpp Discord, r/LocalLLaMA
5. **File issue**: GitHub issues with logs and device specs

---

**Last Updated:** January 30, 2026  
**Version:** 1.0  
**Maintainer:** JARVIS Development Team
