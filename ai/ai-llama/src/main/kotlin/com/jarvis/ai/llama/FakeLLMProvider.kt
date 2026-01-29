package com.jarvis.ai.llama

import com.jarvis.ai.core.LLMProvider
import com.jarvis.ai.core.ModelInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

/**
 * FAKE LLM Provider for POC testing
 *
 * This simulates LLM responses before we integrate llama.cpp.
 * Replace with real LlamaLLMProvider when native integration is ready.
 */
class FakeLLMProvider : LLMProvider {

    private val _isLoaded = MutableStateFlow(false)
    override val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0f)
    override val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()

    private var _modelInfo: ModelInfo? = null
    override val modelInfo: ModelInfo? get() = _modelInfo

    private var isCancelled = false

    override suspend fun load(modelPath: String) {
        _loadingProgress.value = 0f

        // Simulate loading progress
        for (i in 1..10) {
            delay(200)
            _loadingProgress.value = i / 10f
        }

        _modelInfo = ModelInfo(
            name = "FakeLLM (POC)",
            sizeBytes = 0,
            contextLength = 4096
        )
        _isLoaded.value = true
    }

    override suspend fun unload() {
        _isLoaded.value = false
        _modelInfo = null
        _loadingProgress.value = 0f
    }

    override fun generateStream(prompt: String, systemPrompt: String): Flow<String> = flow {
        isCancelled = false

        // Parse the prompt to give contextual responses
        val response = generateFakeResponse(prompt, systemPrompt)

        // Stream word by word
        val words = response.split(" ")
        for (word in words) {
            if (isCancelled) break
            emit("$word ")
            delay(50) // Simulate token generation time
        }
    }

    override fun cancel() {
        isCancelled = true
    }

    private fun generateFakeResponse(prompt: String, systemPrompt: String): String {
        val lowerPrompt = prompt.lowercase()

        // Check for corrections in system prompt
        val hasCorrections = systemPrompt.contains("Corrections")

        return when {
            "hello" in lowerPrompt || "hi" in lowerPrompt ->
                "Hello! I'm JARVIS, your AI assistant. How can I help you today?"

            "how are you" in lowerPrompt ->
                "I'm functioning optimally, thank you for asking! How are you doing?"

            "what can you do" in lowerPrompt ->
                "I'm a proof-of-concept AI assistant. Right now I can chat with you and learn from corrections. More capabilities coming soon!"

            "test" in lowerPrompt ->
                "Test received! The system is working. I'm currently in POC mode - using fake responses until llama.cpp is integrated."

            "learn" in lowerPrompt || "remember" in lowerPrompt ->
                "I learn from our conversations! When you correct me, I store that feedback and use it to improve future responses."

            hasCorrections && "prefer" in lowerPrompt ->
                "I see you have some preferences! I'll do my best to follow them. You can always correct me if I make mistakes."

            else ->
                "I understand you said: \"$prompt\". This is a POC response - real LLM integration coming soon! Feel free to correct me by editing my responses."
        }
    }
}
