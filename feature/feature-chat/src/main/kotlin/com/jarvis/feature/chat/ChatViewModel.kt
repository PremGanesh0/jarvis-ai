package com.jarvis.feature.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.core.LLMProvider
import com.jarvis.ai.llama.DownloadState
import com.jarvis.ai.llama.ModelManager
import com.jarvis.ai.llama.di.USE_REAL_LLM
import com.jarvis.core.model.ChatMessage
import com.jarvis.core.model.MessageRole
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

private const val TAG = "ChatViewModel"

/**
 * ViewModel for the chat screen.
 * Handles model loading, chat, and correction flow.
 */
class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveCorrectionUseCase: SaveCorrectionUseCase,
    private val learningEngine: LearningEngine,
    private val llmProvider: LLMProvider,
    private val modelManager: ModelManager,
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChatEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        Log.d(TAG, "ChatViewModel init - checking model status, useRealLLM=$USE_REAL_LLM")
        checkModelStatus()
        observeModelState()
    }

    private fun checkModelStatus() {
        viewModelScope.launch {
            // For fake LLM, skip model file check and load immediately
            if (!USE_REAL_LLM) {
                Log.d(TAG, "Using fake LLM - loading immediately")
                loadModel()
                return@launch
            }

            val isAvailable = modelManager.isModelAvailable()
            Log.d(TAG, "Model available: $isAvailable")
            if (isAvailable) {
                loadModel()
            } else {
                Log.d(TAG, "Model needs download")
                _uiState.update { it.copy(modelState = ModelState.NeedsDownload) }
            }
        }
    }

    private fun observeModelState() {
        viewModelScope.launch {
            llmProvider.isLoaded.collect { isLoaded ->
                Log.d(TAG, "Model isLoaded: $isLoaded")
                if (isLoaded) {
                    _uiState.update { it.copy(modelState = ModelState.Ready) }
                    _events.send(ChatEvent.ModelReady)
                }
            }
        }
        viewModelScope.launch {
            llmProvider.loadingProgress.collect { progress ->
                Log.d(TAG, "Model loading progress: $progress")
                _uiState.update { it.copy(modelLoadProgress = progress) }
            }
        }
    }

    private fun loadModel() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting model load...")
                _uiState.update { it.copy(modelState = ModelState.Loading) }

                // For fake LLM, we don't need a real model path
                val modelPath = if (USE_REAL_LLM) {
                    modelManager.getModelPath()
                } else {
                    "fake://model"
                }
                Log.d(TAG, "Model path: $modelPath")
                llmProvider.load(modelPath)
                Log.d(TAG, "Model load call completed")
            } catch (e: Exception) {
                Log.e(TAG, "Model load failed", e)
                _uiState.update {
                    it.copy(modelState = ModelState.Error(e.message ?: "Failed to load model"))
                }
            }
        }
    }

    fun onDownloadModel() {
        viewModelScope.launch {
            Log.d(TAG, "Starting model download...")
            _uiState.update { it.copy(isDownloading = true, downloadProgress = 0f) }

            modelManager.downloadModel().collect { state ->
                Log.d(TAG, "Download state: $state")
                when (state) {
                    is DownloadState.Starting -> {
                        _uiState.update { it.copy(downloadProgress = 0f) }
                    }
                    is DownloadState.Progress -> {
                        _uiState.update { it.copy(downloadProgress = state.progress) }
                    }
                    is DownloadState.Completed -> {
                        _uiState.update { it.copy(isDownloading = false) }
                        loadModel()
                    }
                    is DownloadState.Failed -> {
                        _uiState.update {
                            it.copy(
                                isDownloading = false,
                                modelState = ModelState.Error(state.error),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isLoading) return
        if (_uiState.value.modelState != ModelState.Ready) return

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
                // Use the updated SaveCorrectionUseCase signature
                saveCorrectionUseCase(
                    originalResponse = correctionMode.originalResponse,
                    correctedResponse = correctedText,
                    context = "conversation:$conversationId,message:${correctionMode.originalMessageId}",
                )

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

    fun onRetryLoadModel() {
        if (modelManager.isModelAvailable()) {
            loadModel()
        } else {
            _uiState.update { it.copy(modelState = ModelState.NeedsDownload) }
        }
    }
}
