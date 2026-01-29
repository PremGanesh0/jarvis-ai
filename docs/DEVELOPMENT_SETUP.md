# 🛠️ Development Setup Guide

> Complete environment setup for J.A.R.V.I.S. development

---

## 📋 Prerequisites Checklist

### Required Software

| Tool | Version | Purpose | Download |
|------|---------|---------|----------|
| **Android Studio** | Ladybug (2024.2.1) or newer | IDE | [Download](https://developer.android.com/studio) |
| **JDK** | 17 (bundled with AS) | Java runtime | Bundled |
| **Kotlin** | 2.0.21+ | Primary language | Bundled |
| **Android SDK** | API 35 (target), API 29 (min) | Android platform | SDK Manager |
| **NDK** | r27+ | Native C++ compilation | SDK Manager |
| **CMake** | 3.22.1+ | Native build system | SDK Manager |
| **Git** | 2.40+ | Version control | [Download](https://git-scm.com/) |

### Hardware Requirements (Development Machine)

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| **RAM** | 16 GB | 32 GB |
| **Storage** | 50 GB free | 100 GB SSD |
| **CPU** | 4 cores | 8+ cores |
| **OS** | Ubuntu 22.04 / macOS 13 / Windows 11 | Linux preferred for NDK |

### Test Device Requirements

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| **Android** | 10 (API 29) | 14 (API 34) |
| **RAM** | 6 GB | 8+ GB |
| **Storage** | 8 GB free | 16 GB free |
| **SoC** | Snapdragon 6xx | Snapdragon 8 Gen 1+ |

---

## 🚀 Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/your-username/jarvis-ai.git
cd jarvis-ai
```

### 2. Install Android Studio Components

Open Android Studio → Settings → SDK Manager:

```
SDK Platforms:
  ✅ Android 14.0 (API 34)
  ✅ Android 10.0 (API 29)

SDK Tools:
  ✅ Android SDK Build-Tools 35
  ✅ NDK (Side by side) - version 27.x
  ✅ CMake 3.22.1
  ✅ Android SDK Command-line Tools
  ✅ Android Emulator (optional)
```

### 3. Configure local.properties

```properties
# local.properties (auto-generated, but verify)
sdk.dir=/home/YOUR_USER/Android/Sdk
ndk.dir=/home/YOUR_USER/Android/Sdk/ndk/27.0.12077973
```

### 4. Sync and Build

```bash
# Command line
./gradlew assembleDebug

# Or in Android Studio
# Click "Sync Project with Gradle Files" 🐘
# Then Build → Make Project
```

---

## 📁 Project Structure (After Scaffolding)

```
jarvis-ai/
│
├── 📄 settings.gradle.kts          # Module definitions
├── 📄 build.gradle.kts             # Root build config
├── 📄 gradle.properties            # Gradle settings
├── 📁 gradle/
│   └── libs.versions.toml          # Version catalog
│
├── 📦 app/                         # Main Android app
├── 📦 core/                        # Core utilities
│   ├── core-common/
│   ├── core-model/
│   └── core-testing/
├── 📦 domain/                      # Business logic
├── 📦 data/                        # Data layer
├── 📦 ai/                          # AI modules
│   ├── ai-core/                    # Interfaces
│   ├── ai-llama/                   # llama.cpp
│   ├── ai-whisper/                 # whisper.cpp
│   ├── ai-piper/                   # Piper TTS
│   ├── ai-embeddings/              # Embeddings
│   └── ai-rag/                     # RAG pipeline
├── 📦 learning/                    # Learning system
├── 📦 feature/                     # Feature modules
├── 📦 services/                    # Android services
├── 📦 system/                      # System integration
│
├── 📁 models/                      # AI models (git-ignored)
│   ├── llm/
│   ├── stt/
│   ├── tts/
│   └── embeddings/
│
└── 📁 docs/                        # Documentation
```

---

## 🤖 AI Models Setup

### Download Required Models

Models are NOT included in the repository (too large). Download them manually:

```bash
# Create models directory
mkdir -p models/{llm,stt,tts,embeddings}

# Download LLM (Phi-3.5-mini)
# From: https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf
wget -O models/llm/phi-3.5-mini-instruct-q4_k_m.gguf \
  "https://huggingface.co/microsoft/Phi-3.5-mini-instruct-gguf/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf"

# Download Whisper (tiny.en)
# From: https://huggingface.co/ggerganov/whisper.cpp
wget -O models/stt/ggml-tiny.en.bin \
  "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en.bin"

# Download Piper TTS voice
# From: https://github.com/rhasspy/piper/releases
wget -O models/tts/en_GB-alan-medium.onnx \
  "https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_GB/alan/medium/en_GB-alan-medium.onnx"
wget -O models/tts/en_GB-alan-medium.onnx.json \
  "https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_GB/alan/medium/en_GB-alan-medium.onnx.json"

# Download Embedding model
# From: https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2
# (ONNX version needed - will provide script)
```

### Model Sizes Reference

| Model | File | Size |
|-------|------|------|
| Phi-3.5-mini Q4_K_M | `phi-3.5-mini-instruct-q4_k_m.gguf` | ~2.5 GB |
| Whisper tiny.en | `ggml-tiny.en.bin` | ~75 MB |
| Piper Alan | `en_GB-alan-medium.onnx` | ~50 MB |
| MiniLM-L6-v2 | `all-minilm-l6-v2.onnx` | ~25 MB |
| **Total** | | **~2.7 GB** |

### .gitignore for Models

```gitignore
# Already in .gitignore
models/
*.gguf
*.bin
*.onnx
```

---

## 🔧 Native Libraries Setup

### Building llama.cpp for Android

```bash
# Clone llama.cpp
git clone https://github.com/ggerganov/llama.cpp.git
cd llama.cpp

# Build for Android (ARM64)
mkdir build-android && cd build-android

cmake .. \
  -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
  -DANDROID_ABI=arm64-v8a \
  -DANDROID_PLATFORM=android-29 \
  -DLLAMA_NATIVE=OFF \
  -DLLAMA_BUILD_EXAMPLES=OFF \
  -DLLAMA_BUILD_TESTS=OFF

make -j$(nproc)

# Copy to project
cp libllama.so ../../jarvis-ai/ai/ai-llama/src/main/jniLibs/arm64-v8a/
```

### Building whisper.cpp for Android

```bash
# Clone whisper.cpp
git clone https://github.com/ggerganov/whisper.cpp.git
cd whisper.cpp

# Build for Android (ARM64)
mkdir build-android && cd build-android

cmake .. \
  -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
  -DANDROID_ABI=arm64-v8a \
  -DANDROID_PLATFORM=android-29 \
  -DWHISPER_BUILD_EXAMPLES=OFF \
  -DWHISPER_BUILD_TESTS=OFF

make -j$(nproc)

# Copy to project
cp libwhisper.so ../../jarvis-ai/ai/ai-whisper/src/main/jniLibs/arm64-v8a/
```

---

## 🧪 Running the App

### Debug Build

```bash
# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Or use Android Studio Run button
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Specific module tests
./gradlew :domain:test
./gradlew :ai:ai-core:test
```

### Logs and Debugging

```bash
# View JARVIS logs
adb logcat | grep -E "(JARVIS|LlamaJni|WhisperJni)"

# View AI inference logs
adb logcat | grep "GeminiApi\|LlamaProvider"

# Clear app data
adb shell pm clear com.jarvis.ai
```

---

## 🔑 Environment Variables

Create a `.env` file (not committed):

```bash
# .env (add to .gitignore)

# Optional: Porcupine wake word (for "Hey JARVIS")
PORCUPINE_ACCESS_KEY=your_key_here

# Optional: For testing with cloud models during development
# OPENAI_API_KEY=sk-...  # DO NOT COMMIT
```

---

## 🐛 Common Issues

### Issue: NDK not found

```
Solution: Install NDK via SDK Manager
SDK Manager → SDK Tools → NDK (Side by side) → Check version 27.x
```

### Issue: CMake version mismatch

```
Solution: Install correct CMake version
SDK Manager → SDK Tools → CMake → Check 3.22.1
```

### Issue: Out of memory during build

```
Solution: Increase Gradle heap
Edit gradle.properties:
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

### Issue: Model too large for device

```
Solution: Use smaller quantization
- Instead of Q4_K_M, try Q4_0 (smaller but lower quality)
- Or use Phi-3-mini instead of Phi-3.5-mini
```

### Issue: AccessibilityService not working

```
Solution:
1. Grant permission in Settings → Accessibility → JARVIS
2. Restart the app after granting
3. Check logcat for errors
```

---

## 📱 Test Devices

### Recommended Test Devices

| Device | RAM | Why Good for Testing |
|--------|-----|----------------------|
| Pixel 7a | 8 GB | Good baseline, stock Android |
| Samsung S23 | 8 GB | Popular flagship |
| OnePlus 12R | 12 GB | High RAM for LLM |
| Poco F5 | 8 GB | Budget device test |

### Emulator Settings (for UI testing only)

```
# NOT recommended for AI testing (too slow)
# Use only for UI development

AVD Settings:
- Device: Pixel 7
- API: 34
- RAM: 4 GB
- Heap: 512 MB
```

---

## 🔄 Daily Development Workflow

```bash
# 1. Pull latest
git pull origin main

# 2. Sync Gradle
./gradlew --refresh-dependencies

# 3. Run tests
./gradlew test

# 4. Build and install
./gradlew installDebug

# 5. Check logs
adb logcat | grep JARVIS
```

---

## 📚 Useful Commands

```bash
# Clean build
./gradlew clean

# Build all modules
./gradlew build

# Generate dependency report
./gradlew :app:dependencies

# Check for dependency updates
./gradlew dependencyUpdates

# Format code (if using ktlint)
./gradlew ktlintFormat

# Static analysis (if using detekt)
./gradlew detekt
```

---

## ✅ Setup Verification Checklist

Before starting development, verify:

- [ ] Android Studio opens project without errors
- [ ] Gradle sync completes successfully
- [ ] `./gradlew assembleDebug` builds without errors
- [ ] App installs on test device
- [ ] Models are downloaded to `models/` directory
- [ ] Native libraries are in `jniLibs/` directories
- [ ] Test device has 6GB+ RAM
- [ ] Test device has 8GB+ free storage

---

*Last updated: January 2026*
