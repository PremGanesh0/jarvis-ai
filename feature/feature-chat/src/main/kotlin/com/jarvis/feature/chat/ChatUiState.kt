package com.jarvis.feature.chat

import com.jarvis.core.model.ChatMessage

/**
 * UI state for the chat screen.
 * Following MVI pattern - single immutable state object.
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val streamingText: String = "",
    val error: String? = null,
    val correctionMode: CorrectionMode? = null,
    // Model loading state
    val modelState: ModelState = ModelState.NotLoaded,
    val modelLoadProgress: Float = 0f,
    val downloadProgress: Float = 0f,
    val isDownloading: Boolean = false,
)

/**
 * State of the LLM model
 */
sealed interface ModelState {
    data object NotLoaded : ModelState
    data object Loading : ModelState
    data object Ready : ModelState
    data object NeedsDownload : ModelState
    data class Error(val message: String) : ModelState
}

/**
 * Represents when user is correcting an AI response.
 */
data class CorrectionMode(
    val originalMessageId: String,
    val originalResponse: String,
)

/**
 * One-time events from the ViewModel.
 */
sealed interface ChatEvent {
    data object ScrollToBottom : ChatEvent
    data class ShowError(val message: String) : ChatEvent
    data object CorrectionSaved : ChatEvent
    data object ModelReady : ChatEvent
}
