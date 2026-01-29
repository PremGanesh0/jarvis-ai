package com.jarvis.feature.chat

import com.jarvis.coremodel.ChatMessage

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
)

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
}
