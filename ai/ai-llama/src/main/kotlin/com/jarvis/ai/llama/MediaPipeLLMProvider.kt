package com.jarvis.ai.llama

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.jarvis.ai.core.LLMProvider
import com.jarvis.ai.core.ModelInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

/**
 * LLM Provider using Google MediaPipe LLM Inference API.
 *
 * This is the official Android solution for on-device LLM inference.
 * Supports Gemma models in .task format.
 */
class MediaPipeLLMProvider(
    private val context: Context,
) : LLMProvider {

    companion object {
        private const val TAG = "MediaPipeLLMProvider"

        // Inference parameters
        private const val DEFAULT_MAX_TOKENS = 512
        private const val DEFAULT_TEMPERATURE = 0.7f
        private const val DEFAULT_CONTEXT_LENGTH = 4096
    }

    private val _isLoaded = MutableStateFlow(false)
    override val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0f)
    override val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()

    private var _modelInfo: ModelInfo? = null
    override val modelInfo: ModelInfo? get() = _modelInfo

    private var llmInference: LlmInference? = null
    private var isCancelled = false
    private var currentModelPath: String? = null

    override suspend fun load(modelPath: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Loading model from: $modelPath")
                _loadingProgress.value = 0.1f

                val file = File(modelPath)
                if (!file.exists()) {
                    throw IllegalArgumentException("Model file not found: $modelPath")
                }

                _loadingProgress.value = 0.3f

                // Build LLM Inference options
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelPath)
                    .setMaxTokens(DEFAULT_MAX_TOKENS)
                    .build()

                _loadingProgress.value = 0.5f

                // Create LLM Inference instance
                llmInference = LlmInference.createFromOptions(context, options)

                _loadingProgress.value = 0.9f

                // Extract model info
                _modelInfo = ModelInfo(
                    name = file.nameWithoutExtension,
                    sizeBytes = file.length(),
                    contextLength = DEFAULT_CONTEXT_LENGTH,
                )

                currentModelPath = modelPath
                _loadingProgress.value = 1.0f
                _isLoaded.value = true

                Log.i(TAG, "Model loaded successfully: ${_modelInfo?.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load model", e)
                _isLoaded.value = false
                _loadingProgress.value = 0f
                throw e
            }
        }
    }

    override suspend fun unload() {
        withContext(Dispatchers.IO) {
            try {
                llmInference?.close()
                llmInference = null
                _isLoaded.value = false
                _modelInfo = null
                _loadingProgress.value = 0f
                currentModelPath = null
                Log.i(TAG, "Model unloaded")
            } catch (e: Exception) {
                Log.e(TAG, "Error unloading model", e)
            }
        }
    }

    override fun generateStream(prompt: String, systemPrompt: String): Flow<String> = flow {
        val inference = llmInference ?: throw IllegalStateException("Model not loaded")
        isCancelled = false

        // Build the full prompt
        val fullPrompt = buildPrompt(prompt, systemPrompt)

        Log.d(TAG, "Generating response for prompt length: ${fullPrompt.length}")

        try {
            // MediaPipe's sync API returns complete response
            val result = inference.generateResponse(fullPrompt)

            if (!isCancelled && result != null) {
                // Emit the full response
                // For streaming effect, we emit word by word
                val words = result.split(" ")
                for (word in words) {
                    if (isCancelled) break
                    emit("$word ")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during generation", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    override fun cancel() {
        isCancelled = true
        Log.d(TAG, "Cancel requested")
    }

    /**
     * Build prompt for the model.
     * MediaPipe models often have their chat template built in.
     */
    private fun buildPrompt(userMessage: String, systemPrompt: String): String {
        val systemContent = systemPrompt.ifBlank {
            "You are J.A.R.V.I.S., a helpful AI assistant. Be concise and helpful."
        }

        // Simple prompt format - Gemma models usually handle this well
        return "System: $systemContent\n\nUser: $userMessage\n\nAssistant:"
    }
}
