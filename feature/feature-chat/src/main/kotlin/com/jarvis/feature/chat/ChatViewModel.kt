package com.jarvis.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.coremodel.ChatMessage
import com.jarvis.coremodel.Correction
import com.jarvis.coremodel.MessageRole
import com.jarvis.domain.usecase.SaveCorrectionUseCase
import com.jarvis.domain.usecase.SendMessageUseCase
import com.jarvis.learning.LearningEngine
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the chat screen.
 * Handles user input, message sending, and correction flow.
 */
class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveCorrectionUseCase: SaveCorrectionUseCase,
    private val learningEngine: LearningEngine,
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChatEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isLoading) return

        // Clear input and set loading
        _uiState.update {
            it.copy(
                inputText = "",
                isLoading = true,
                isStreaming = true,
                streamingText = "",
                error = null,
            )
        }

        // Add user message to UI immediately
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = text,
            timestamp = System.currentTimeMillis(),
            conversationId = conversationId,
        )

        _uiState.update {
            it.copy(messages = it.messages + userMessage)
        }

        viewModelScope.launch {
            try {
                sendMessageUseCase(conversationId, text).collect { chunk ->
                    _uiState.update {
                        it.copy(streamingText = it.streamingText + chunk)
                    }
                }

                // Streaming complete - create assistant message
                val assistantMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.ASSISTANT,
                    content = _uiState.value.streamingText,
                    timestamp = System.currentTimeMillis(),
                    conversationId = conversationId,
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + assistantMessage,
                        isLoading = false,
                        isStreaming = false,
                        streamingText = "",
                    )
                }

                _events.send(ChatEvent.ScrollToBottom)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isStreaming = false,
                        error = e.message ?: "Unknown error",
                    )
                }
                _events.send(ChatEvent.ShowError(e.message ?: "Failed to send message"))
            }
        }
    }

    fun onStartCorrection(messageId: String) {
        val message = _uiState.value.messages.find { it.id == messageId } ?: return
        if (message.role != MessageRole.ASSISTANT) return

        _uiState.update {
            it.copy(
                correctionMode = CorrectionMode(
                    originalMessageId = messageId,
                    originalResponse = message.content,
                ),
                inputText = message.content, // Pre-fill with original for editing
            )
        }
    }

    fun onSubmitCorrection() {
        val correctionMode = _uiState.value.correctionMode ?: return
        val correctedText = _uiState.value.inputText.trim()
        if (correctedText.isEmpty()) return

        viewModelScope.launch {
            try {
                val correction = Correction(
                    id = UUID.randomUUID().toString(),
                    originalResponse = correctionMode.originalResponse,
                    correctedResponse = correctedText,
                    timestamp = System.currentTimeMillis(),
                    category = "user_correction",
                    priority = 1.0f, // User corrections are high priority
                    conversationId = conversationId,
                    messageId = correctionMode.originalMessageId,
                )

                saveCorrectionUseCase(correction)

                // Update the message in the list
                _uiState.update { state ->
                    val updatedMessages = state.messages.map { msg ->
                        if (msg.id == correctionMode.originalMessageId) {
                            msg.copy(content = correctedText)
                        } else {
                            msg
                        }
                    }
                    state.copy(
                        messages = updatedMessages,
                        correctionMode = null,
                        inputText = "",
                    )
                }

                _events.send(ChatEvent.CorrectionSaved)
            } catch (e: Exception) {
                _events.send(ChatEvent.ShowError("Failed to save correction"))
            }
        }
    }

    fun onCancelCorrection() {
        _uiState.update {
            it.copy(
                correctionMode = null,
                inputText = "",
            )
        }
    }

    fun onDismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
