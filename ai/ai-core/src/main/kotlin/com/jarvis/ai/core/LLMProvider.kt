package com.jarvis.ai.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for LLM providers - can be swapped for different models
 */
interface LLMProvider {

    /** Whether the model is loaded and ready */
    val isLoaded: StateFlow<Boolean>

    /** Loading progress (0.0 to 1.0) */
    val loadingProgress: StateFlow<Float>

    /** Model information */
    val modelInfo: ModelInfo?

    /**
     * Load the model from the given path
     */
    suspend fun load(modelPath: String)

    /**
     * Unload the model to free memory
     */
    suspend fun unload()

    /**
     * Generate a streaming response
     * @param prompt The user's message
     * @param systemPrompt System prompt with learnings/context
     * @return Flow of generated tokens
     */
    fun generateStream(
        prompt: String,
        systemPrompt: String = ""
    ): Flow<String>

    /**
     * Cancel any ongoing generation
     */
    fun cancel()
}

data class ModelInfo(
    val name: String,
    val sizeBytes: Long,
    val contextLength: Int
)
