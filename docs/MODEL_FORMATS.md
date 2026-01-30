# 📋 AI Model Formats - Technical Specification

> **Deep dive into GGUF, ONNX, and GGML formats for on-device AI**

This document provides technical details about AI model formats used in JARVIS, including binary structure, quantization techniques, optimization strategies, and implementation guidelines.

---

## 📋 Table of Contents

1. [Format Overview](#format-overview)
2. [GGUF Format (LLM)](#gguf-format-llm)
3. [ONNX Format (TTS, Embeddings)](#onnx-format-tts-embeddings)
4. [GGML Format (Whisper STT)](#ggml-format-whisper-stt)
5. [Quantization Deep Dive](#quantization-deep-dive)
6. [Performance Optimization](#performance-optimization)
7. [Implementation Guide](#implementation-guide)

---

## 🎯 Format Overview

### Why Multiple Formats?

```
┌─────────────────────────────────────────────────────────────┐
│              FORMAT SELECTION RATIONALE                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  GGUF (LLM):           ✅ Optimized for autoregressive      │
│                        ✅ Memory-mapped loading             │
│                        ✅ Efficient token generation        │
│                                                             │
│  ONNX (TTS/Embed):     ✅ Cross-platform standard          │
│                        ✅ Hardware accelerated              │
│                        ✅ Fixed input/output                │
│                                                             │
│  GGML (Whisper):       ✅ Purpose-built for audio          │
│                        ✅ Optimized for whisper.cpp         │
│                        ✅ Proven on mobile                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Format Comparison

| Feature | GGUF | ONNX | GGML |
|---------|------|------|------|
| **Primary Use** | LLM inference | Neural nets | Audio models |
| **File Size** | Variable (with quantization) | Fixed | Fixed |
| **Memory Mapping** | ✅ Yes | ⚠️ Partial | ✅ Yes |
| **Quantization** | ✅ Built-in | ❌ External | ✅ Built-in |
| **Streaming** | ✅ Token-by-token | ❌ Batch only | ⚠️ Chunk-based |
| **GPU Support** | ✅ Partial offload | ✅ Full acceleration | ⚠️ CPU optimized |
| **Mobile Optimized** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Cross-platform** | ✅ Yes | ✅ Yes | ✅ Yes |

---

## 🔷 GGUF Format (LLM)

### What is GGUF?

```
GGUF = GPT-Generated Unified Format
Successor to GGML format (used for Whisper)
Designed specifically for large language models
```

### File Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    GGUF FILE STRUCTURE                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [HEADER]                                                   │
│  ├─ Magic Number: 0x46554747 ("GGUF")                     │
│  ├─ Version: 3                                             │
│  ├─ Tensor Count: N                                        │
│  └─ Metadata Count: M                                      │
│                                                             │
│  [METADATA]                                                 │
│  ├─ Model Architecture (e.g., "phi3")                      │
│  ├─ Context Length (e.g., 131072)                          │
│  ├─ Embedding Length (e.g., 3072)                          │
│  ├─ Block Count (e.g., 32)                                 │
│  ├─ Attention Head Count (e.g., 32)                        │
│  ├─ Rope Frequency Base                                    │
│  ├─ Tokenizer Type (e.g., "llama")                         │
│  ├─ Tokenizer Vocab                                        │
│  └─ ... (more metadata)                                    │
│                                                             │
│  [TENSOR INFO]                                              │
│  ├─ Tensor 0: name, dimensions, type, offset               │
│  ├─ Tensor 1: name, dimensions, type, offset               │
│  └─ ... (all N tensors)                                    │
│                                                             │
│  [TENSOR DATA]                                              │
│  ├─ Aligned to 32 bytes                                    │
│  ├─ Tensor 0 data (quantized)                              │
│  ├─ Tensor 1 data (quantized)                              │
│  └─ ... (all tensor weights)                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Metadata Keys (Important Ones)

```python
# Model Architecture
"general.architecture"        → "phi3", "llama", "gemma", etc.
"general.name"                → Human-readable name
"general.file_type"           → Quantization type (Q4_K_M = 15)

# Model Dimensions
"{arch}.context_length"       → Maximum context tokens
"{arch}.embedding_length"     → Hidden dimension size
"{arch}.block_count"          → Number of transformer layers
"{arch}.feed_forward_length"  → FFN dimension
"{arch}.attention.head_count" → Number of attention heads
"{arch}.attention.head_count_kv" → KV heads (for GQA)

# RoPE Configuration (for positional encoding)
"{arch}.rope.dimension_count" → RoPE dimensions
"{arch}.rope.freq_base"       → Frequency base (e.g., 10000.0)

# Tokenizer
"tokenizer.ggml.model"        → Tokenizer type ("llama", "gpt2")
"tokenizer.ggml.tokens"       → Vocabulary list
"tokenizer.ggml.scores"       → Token scores
"tokenizer.ggml.token_type"   → Token types (normal, control, etc.)
"tokenizer.ggml.bos_token_id" → Beginning of sequence token
"tokenizer.ggml.eos_token_id" → End of sequence token
```

### Quantization Types in GGUF

```cpp
// Quantization type enumeration
enum ggml_type {
    GGML_TYPE_F32  = 0,   // float32 (4 bytes per weight)
    GGML_TYPE_F16  = 1,   // float16 (2 bytes per weight)
    GGML_TYPE_Q4_0 = 2,   // 4-bit quantization (block of 32)
    GGML_TYPE_Q4_1 = 3,   // 4-bit quantization with scale per block
    GGML_TYPE_Q5_0 = 6,   // 5-bit quantization
    GGML_TYPE_Q5_1 = 7,   // 5-bit quantization with scale
    GGML_TYPE_Q8_0 = 8,   // 8-bit quantization
    GGML_TYPE_Q8_1 = 9,   // 8-bit quantization with scale
    
    // K-quants (better quality)
    GGML_TYPE_Q2_K = 10,  // 2-bit super-quantization
    GGML_TYPE_Q3_K = 11,  // 3-bit super-quantization
    GGML_TYPE_Q4_K = 12,  // 4-bit super-quantization
    GGML_TYPE_Q5_K = 13,  // 5-bit super-quantization
    GGML_TYPE_Q6_K = 14,  // 6-bit super-quantization
    GGML_TYPE_Q8_K = 15,  // 8-bit super-quantization
};
```

### K-Quant Variants

```
Q4_K_S → Small variant (maximum compression)
Q4_K_M → Medium variant (recommended balance)
Q4_K_L → Large variant (better quality)

How K-quants work:
- Divide weights into blocks
- Use different quantization for different importance
- Important layers (attention): higher precision
- Less important layers (FFN): lower precision
- Result: Better quality at same size
```

### Memory Layout

```
For a 3.8B parameter model (Phi-3.5-mini):

FP32 (no quantization):
  3.8B params × 4 bytes = 15.2 GB

FP16 (half precision):
  3.8B params × 2 bytes = 7.6 GB

Q8_0 (8-bit):
  3.8B params × 1 byte = 3.8 GB

Q4_K_M (4-bit mixed):
  3.8B params × 0.5 bytes (avg) = 1.9 GB on disk
  + overhead for scales and metadata = 2.5 GB actual
```

### Loading GGUF in Code

```kotlin
// JNI interface to llama.cpp
class LlamaModel {
    private external fun loadModel(
        path: String,
        contextSize: Int,
        gpuLayers: Int
    ): Long  // Returns model handle
    
    fun load(modelPath: String): LlamaModel {
        // Memory-mapped loading (fast, efficient)
        val handle = loadModel(
            path = modelPath,
            contextSize = 8192,
            gpuLayers = 0  // CPU only on most Android devices
        )
        
        return LlamaModel(handle)
    }
}
```

### GGUF Advantages

| Advantage | Benefit |
|-----------|---------|
| **Single File** | Easy distribution and management |
| **Memory Mapping** | Fast loading without copying to RAM |
| **Self-Contained** | Includes tokenizer and config |
| **Quantization Built-in** | No external tools needed |
| **Backward Compatible** | Can read older GGML files |
| **Metadata Rich** | All model info in one place |

---

## 🔶 ONNX Format (TTS, Embeddings)

### What is ONNX?

```
ONNX = Open Neural Network Exchange
Cross-platform, framework-agnostic format
Optimized for inference (not training)
Industry standard (Microsoft, Facebook, AWS, etc.)
```

### File Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    ONNX FILE STRUCTURE                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [PROTOBUF HEADER]                                          │
│  ├─ IR Version: 8                                           │
│  ├─ Producer: "pytorch" or "tf2onnx"                        │
│  └─ Model Version: 1                                        │
│                                                             │
│  [GRAPH]                                                    │
│  ├─ Input Tensors                                           │
│  │   ├─ Name: "input"                                       │
│  │   ├─ Shape: [batch, sequence_length]                     │
│  │   └─ Type: int64                                         │
│  │                                                           │
│  ├─ Operations (nodes)                                      │
│  │   ├─ Op 1: MatMul                                        │
│  │   ├─ Op 2: Add                                           │
│  │   ├─ Op 3: ReLU                                          │
│  │   └─ ... (hundreds of ops)                               │
│  │                                                           │
│  ├─ Output Tensors                                          │
│  │   ├─ Name: "output"                                      │
│  │   ├─ Shape: [batch, embedding_dim]                       │
│  │   └─ Type: float32                                       │
│  │                                                           │
│  └─ Initializers (weights)                                  │
│      ├─ Weight tensor 1                                     │
│      ├─ Weight tensor 2                                     │
│      └─ ... (all learned weights)                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Example: Piper TTS ONNX

```json
// en_GB-alan-medium.onnx.json (config file)
{
  "audio": {
    "sample_rate": 22050,          // Output audio sample rate
    "quality": "medium"
  },
  "inference": {
    "noise_scale": 0.667,          // Variability in speech
    "length_scale": 1.0,           // Speech speed
    "noise_w": 0.8                 // Phoneme duration variability
  },
  "phoneme_id_map": {              // Text to phoneme mapping
    "a": [0],
    "b": [1],
    // ... all phonemes
  },
  "num_speakers": 1,               // Single voice
  "speaker_id_map": {
    "default": 0
  }
}
```

### ONNX Operators Used

Common operators in TTS/Embedding models:

```
# Linear layers
MatMul, Add, Gemm

# Activations
ReLU, Tanh, Sigmoid, GELU, Softmax

# Normalization
BatchNormalization, LayerNormalization

# Convolution (for audio)
Conv1d, Conv2d

# Attention (for transformers)
MatMul + Softmax + MatMul pattern

# Reshaping
Reshape, Transpose, Concat, Split

# Math
Sqrt, Div, Mul, Sub
```

### Loading ONNX in Code

```kotlin
// Using ONNX Runtime for Android
class PiperTTS {
    private val env = OrtEnvironment.getEnvironment()
    private lateinit var session: OrtSession
    
    fun load(modelPath: String, configPath: String) {
        // Load config
        val config = File(configPath).readText()
        val configJson = Json.parseToJsonElement(config)
        
        // Load ONNX model
        val options = OrtSession.SessionOptions()
        options.setIntraOpNumThreads(4)  // Use 4 CPU threads
        options.setInterOpNumThreads(1)
        
        session = env.createSession(modelPath, options)
        
        // Prepare for inference
        log("Model loaded: ${session.inputNames}")
    }
    
    fun synthesize(text: String): FloatArray {
        // Convert text to phonemes
        val phonemes = textToPhonemes(text)
        
        // Create input tensor
        val inputTensor = OrtUtil.createTensor(
            env, 
            phonemes.map { it.toLong() }.toLongArray(),
            longArrayOf(1, phonemes.size.toLong())
        )
        
        // Run inference
        val outputs = session.run(
            mapOf("input" to inputTensor)
        )
        
        // Extract audio
        val audio = outputs[0].value as Array<FloatArray>
        return audio[0]  // Return audio samples
    }
}
```

### ONNX Advantages

| Advantage | Benefit |
|-----------|---------|
| **Cross-Platform** | Works on Android, iOS, Desktop, Web |
| **Hardware Acceleration** | Can use GPU, NPU, DSP |
| **Framework Agnostic** | Convert from PyTorch, TF, JAX |
| **Optimized Inference** | Faster than native frameworks |
| **Small Runtime** | ONNX Runtime is lightweight |
| **Industry Standard** | Wide adoption and support |

---

## 🔹 GGML Format (Whisper STT)

### What is GGML?

```
GGML = Georgi Gerganov Machine Learning
Created by Georgi Gerganov (llama.cpp author)
Predecessor to GGUF
Optimized for Whisper models
```

### File Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    GGML FILE STRUCTURE                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [HEADER]                                                   │
│  ├─ Magic Number: 0x67676d6c ("ggml")                     │
│  ├─ Version: 1                                             │
│  ├─ Model Type: Whisper                                    │
│  └─ Quantization: Q5_1                                     │
│                                                             │
│  [HYPERPARAMETERS]                                          │
│  ├─ n_vocab: 51865 (vocabulary size)                       │
│  ├─ n_audio_ctx: 1500 (audio context)                      │
│  ├─ n_audio_state: 384/768 (model size)                    │
│  ├─ n_audio_head: 6/12 (attention heads)                   │
│  ├─ n_audio_layer: 4/12 (encoder layers)                   │
│  ├─ n_text_ctx: 448 (text context)                         │
│  ├─ n_text_state: 384/768 (text model size)                │
│  ├─ n_text_head: 6/12 (text attention heads)               │
│  ├─ n_text_layer: 4/12 (decoder layers)                    │
│  ├─ n_mels: 80 (mel spectrogram bins)                      │
│  └─ ftype: quantization type                               │
│                                                             │
│  [MEL FILTERS]                                              │
│  └─ 80 × 201 matrix for mel spectrogram                     │
│                                                             │
│  [VOCABULARY]                                               │
│  ├─ Token 0: "<|endoftext|>"                               │
│  ├─ Token 1: "<|startoftranscript|>"                       │
│  └─ ... (all 51,865 tokens)                                │
│                                                             │
│  [WEIGHTS]                                                  │
│  ├─ Encoder weights (audio → features)                     │
│  └─ Decoder weights (features → text)                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Whisper Model Sizes

| Model | Params | English-only | Multilingual | Size (GGML) |
|-------|--------|--------------|--------------|-------------|
| **tiny** | 39M | ✅ ggml-tiny.en.bin | ❌ ggml-tiny.bin | 75 MB |
| **base** | 74M | ✅ ggml-base.en.bin | ✅ ggml-base.bin | 142 MB |
| **small** | 244M | ✅ ggml-small.en.bin | ✅ ggml-small.bin | 466 MB |
| **medium** | 769M | ✅ ggml-medium.en.bin | ✅ ggml-medium.bin | 1.5 GB |
| **large** | 1550M | ❌ N/A | ✅ ggml-large-v3.bin | 3.1 GB |

### Loading GGML (Whisper) in Code

```kotlin
// JNI interface to whisper.cpp
class WhisperModel {
    private external fun loadModel(path: String): Long
    private external fun transcribe(
        handle: Long,
        samples: FloatArray,
        sampleRate: Int
    ): String
    
    fun load(modelPath: String): WhisperModel {
        val handle = loadModel(modelPath)
        return WhisperModel(handle)
    }
    
    fun transcribeAudio(audioData: ByteArray): String {
        // Convert audio to float samples
        val samples = audioToFloatSamples(audioData)
        
        // Transcribe
        return transcribe(handle, samples, 16000)
    }
    
    private fun audioToFloatSamples(audio: ByteArray): FloatArray {
        // Convert PCM16 to float32 samples
        val samples = FloatArray(audio.size / 2)
        for (i in samples.indices) {
            val sample = (audio[i * 2].toInt() and 0xFF) or 
                        (audio[i * 2 + 1].toInt() shl 8)
            samples[i] = sample.toShort() / 32768.0f
        }
        return samples
    }
}
```

### GGML Advantages for Whisper

| Advantage | Benefit |
|-----------|---------|
| **Optimized for Audio** | Special handling for mel spectrograms |
| **Fast Inference** | Real-time transcription on mobile |
| **Small Size** | Quantized for mobile devices |
| **Proven Stability** | Widely used in production |
| **CPU Optimized** | Works well without GPU |

---

## ⚙️ Quantization Deep Dive

### What is Quantization?

```
Quantization = Reducing numerical precision of weights

FP32 (32-bit float):  [-3.402823e38, 3.402823e38]
                      ~7 decimal digits precision

FP16 (16-bit float):  [-65504, 65504]
                      ~3 decimal digits precision

INT8 (8-bit integer): [-128, 127]
                      Integer values only

Q4 (4-bit):           [0, 15] with scaling factor
                      16 possible values per weight
```

### Quantization Methods

#### 1. Linear Quantization

```
Original weights: [0.523, -0.234, 0.891, -0.456]

Step 1: Find min/max
  min = -0.456
  max = 0.891

Step 2: Calculate scale
  scale = (max - min) / 255
  scale = 1.347 / 255 = 0.00528

Step 3: Quantize
  q = round((value - min) / scale)
  
  0.523  → round((0.523 - (-0.456)) / 0.00528) = 185
  -0.234 → round((-.234 - (-0.456)) / 0.00528) = 42
  0.891  → round((0.891 - (-0.456)) / 0.00528) = 255
  -0.456 → round((-.456 - (-0.456)) / 0.00528) = 0

Quantized: [185, 42, 255, 0] + scale + zero_point

Storage: 4 bytes (INT8) + 8 bytes (metadata) = 12 bytes
vs Original: 16 bytes (FP32)
Savings: 25%
```

#### 2. K-Quantization (Advanced)

```
K-quantization uses different precision for different weights

Example Q4_K_M:

Block size: 256 weights per block

For each block:
  1. Identify "important" weights (large gradients)
  2. Use 5-6 bits for important weights
  3. Use 3-4 bits for less important weights
  4. Store per-block scale and zero-point

Result: Better quality than uniform Q4 at same size
```

### Quality vs Size Trade-off

```
┌─────────────────────────────────────────────────────────────┐
│           QUANTIZATION QUALITY vs SIZE                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Quality ▲                                                  │
│         │                                                   │
│   100%  ●  FP32 (15.2 GB)                                  │
│         │                                                   │
│    99%  ●  FP16 (7.6 GB)                                   │
│         │                                                   │
│    98%  ●  Q8_0 (3.8 GB)                                   │
│         │                                                   │
│    96%  │    ● Q6_K (2.9 GB)                               │
│         │                                                   │
│    95%  │      ● Q5_K_M (2.6 GB)                           │
│         │                                                   │
│    93%  │         ● Q4_K_M (2.5 GB) ← RECOMMENDED          │
│         │                                                   │
│    90%  │            ● Q4_K_S (2.2 GB)                     │
│         │                                                   │
│    85%  │               ● Q3_K_M (1.9 GB)                  │
│         │                                                   │
│    75%  │                  ● Q2_K (1.5 GB)                 │
│         └──────────────────────────────────────────────►   │
│                                              Size          │
└─────────────────────────────────────────────────────────────┘
```

### Quantization Impact on Performance

| Metric | FP32 | FP16 | Q8_0 | Q4_K_M |
|--------|------|------|------|--------|
| **File Size** | 15.2 GB | 7.6 GB | 3.8 GB | 2.5 GB |
| **RAM Usage** | 16.0 GB | 8.5 GB | 4.5 GB | 3.2 GB |
| **Load Time** | 45s | 25s | 12s | 8s |
| **Tokens/sec** | 5 | 7 | 10 | 12 |
| **Quality Loss** | 0% | <1% | ~2% | ~5-7% |
| **Perplexity** | 5.5 | 5.52 | 5.61 | 5.78 |

### When to Use Each Quantization

| Quantization | Best For | Avoid When |
|--------------|----------|------------|
| **FP32** | Research, training | Always avoid on mobile |
| **FP16** | High-end GPUs | Mobile devices |
| **Q8_0** | High-end mobile (8GB+) | Storage limited |
| **Q6_K** | Quality-critical tasks | Storage limited |
| **Q5_K_M** | Good quality needed | Mid-range devices |
| **Q4_K_M** | **Default choice** (balance) | Never (best option) |
| **Q4_K_S** | Storage very limited | Quality critical |
| **Q3_K_M** | Ultra low-end devices | Accuracy important |
| **Q2_K** | Experimental only | Production use |

---

## 🚀 Performance Optimization

### 1. Memory-Mapped File Access

```kotlin
// Instead of loading entire file into RAM
// Use memory-mapped files

import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

fun loadModelMemoryMapped(path: String): ByteBuffer {
    val channel = FileChannel.open(
        Paths.get(path),
        StandardOpenOption.READ
    )
    
    // Map file to memory
    val buffer = channel.map(
        FileChannel.MapMode.READ_ONLY,
        0,
        channel.size()
    )
    
    // OS handles paging automatically
    return buffer
}

// Benefits:
// - No full file copy to RAM
// - OS manages memory efficiently
// - Faster startup (no loading time)
// - Lower memory pressure
```

### 2. KV Cache Optimization

```kotlin
// Key-Value cache for transformer attention

class KVCache {
    private val k_cache = FloatArray(layers * max_ctx * n_embd)
    private val v_cache = FloatArray(layers * max_ctx * n_embd)
    private var cached_tokens = 0
    
    fun generate(new_tokens: IntArray): String {
        // Reuse cached KV for previous tokens
        // Only compute for new tokens
        
        val start_pos = cached_tokens
        for (token in new_tokens) {
            // Compute only from cached position
            forward_pass(token, start_pos)
            cached_tokens++
        }
    }
}

// Benefits:
// - Don't recompute attention for old tokens
// - 10-50x speedup for multi-turn conversations
// - Memory cost: ~100-300 MB for 8K context
```

### 3. Batch Processing (for Embeddings/TTS)

```kotlin
// Process multiple inputs in one forward pass

fun embedMultiple(texts: List<String>): List<FloatArray> {
    // Batch size 32
    return texts.chunked(32).flatMap { batch ->
        // Tokenize all
        val inputs = batch.map { tokenize(it) }
        
        // Pad to same length
        val maxLen = inputs.maxOf { it.size }
        val padded = inputs.map { it + IntArray(maxLen - it.size) }
        
        // Single ONNX inference for all 32
        val batchTensor = createBatchTensor(padded)
        val outputs = model.run(batchTensor)
        
        // Extract individual embeddings
        outputs.toList()
    }
}

// Benefits:
// - 10-30x faster than individual calls
// - Better hardware utilization
// - Important for RAG similarity search
```

### 4. Float16 Computation (when available)

```kotlin
// Use FP16 for computation if hardware supports
// (Snapdragon 8xx series, Tensor cores)

val options = OrtSession.SessionOptions()
options.addConfigEntry(
    "session.use_fp16_compute",
    "1"  // Enable FP16 computation
)

// Benefits:
// - 2x faster on compatible hardware
// - Lower power consumption
// - No quality loss (computation only)
// - Model stays Q4_K_M
```

---

## 🛠️ Implementation Guide

### Loading Different Formats

#### GGUF (llama.cpp)

```kotlin
// Native JNI binding
external fun llama_load_model(
    path: String,
    params: LlamaParams
): Long

data class LlamaParams(
    val n_ctx: Int = 8192,           // Context size
    val n_batch: Int = 512,          // Batch size
    val n_threads: Int = 4,          // CPU threads
    val n_gpu_layers: Int = 0,       // GPU layers (0 = CPU only)
    val use_mmap: Boolean = true,    // Memory mapping
    val use_mlock: Boolean = false,  // Lock in RAM
    val vocab_only: Boolean = false, // Load vocab only
    val rope_freq_base: Float = 10000.0f,
    val rope_freq_scale: Float = 1.0f
)

// Load model
val handle = llama_load_model(
    "/path/to/model.gguf",
    LlamaParams(
        n_ctx = 8192,
        n_threads = 4,
        use_mmap = true
    )
)
```

#### ONNX (ONNX Runtime)

```kotlin
import ai.onnxruntime.*

val env = OrtEnvironment.getEnvironment()
val options = OrtSession.SessionOptions()

// Optimization
options.setOptimizationLevel(
    OrtSession.SessionOptions.OptLevel.ALL_OPT
)
options.setIntraOpNumThreads(4)
options.setInterOpNumThreads(1)

// Load model
val session = env.createSession(
    "/path/to/model.onnx",
    options
)

// Inference
val input = OrtUtil.createTensor(env, data, shape)
val output = session.run(mapOf("input" to input))
val result = output[0].value as Array<FloatArray>
```

#### GGML (whisper.cpp)

```kotlin
// Native JNI binding
external fun whisper_init_from_file(path: String): Long
external fun whisper_full(
    ctx: Long,
    params: WhisperParams,
    samples: FloatArray,
    n_samples: Int
): Int
external fun whisper_full_get_segment_text(
    ctx: Long,
    i_segment: Int
): String

data class WhisperParams(
    val n_threads: Int = 4,
    val language: String = "en",
    val translate: Boolean = false,
    val print_progress: Boolean = false,
    val print_timestamps: Boolean = false
)

// Load and transcribe
val ctx = whisper_init_from_file("/path/to/ggml-tiny.en.bin")
val params = WhisperParams(n_threads = 4, language = "en")
whisper_full(ctx, params, audioSamples, audioSamples.size)

// Get result
val n_segments = whisper_full_n_segments(ctx)
val transcript = (0 until n_segments).joinToString(" ") {
    whisper_full_get_segment_text(ctx, it)
}
```

---

## 📚 References

### Format Specifications

- [GGUF Specification](https://github.com/ggerganov/ggml/blob/master/docs/gguf.md)
- [ONNX Specification](https://onnx.ai/onnx/intro/concepts.html)
- [llama.cpp Documentation](https://github.com/ggerganov/llama.cpp)
- [whisper.cpp Documentation](https://github.com/ggerganov/whisper.cpp)
- [ONNX Runtime Documentation](https://onnxruntime.ai/docs/)

### Quantization Research

- [GPTQ: Accurate Post-Training Quantization](https://arxiv.org/abs/2210.17323)
- [LLM.int8(): 8-bit Matrix Multiplication](https://arxiv.org/abs/2208.07339)
- [AWQ: Activation-aware Weight Quantization](https://arxiv.org/abs/2306.00978)

---

**Last Updated:** January 30, 2026  
**Version:** 1.0  
**Maintainer:** JARVIS Development Team
