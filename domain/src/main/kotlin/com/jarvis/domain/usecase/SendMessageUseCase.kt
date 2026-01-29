package com.jarvis.domain.usecase

import com.jarvis.ai.core.LLMProvider
import com.jarvis.core.model.ChatMessage
import com.jarvis.core.model.MessageRole
import com.jarvis.data.repository.LearningRepository
import com.jarvis.data.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.util.UUID

/**
 * Use case for sending a message and getting AI response
 */
class SendMessageUseCase(
    private val llmProvider: LLMProvider,
    private val messageRepository: MessageRepository,
    private val learningRepository: LearningRepository
) {
    /**
     * Send a message and get streaming response
     * @param conversationId The conversation ID
     * @param userMessage The user's message text
     * @return Flow of response tokens
     */
    suspend operator fun invoke(
        conversationId: String,
        userMessage: String
    ): Flow<String> {
        // Save user message
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = userMessage
        )
        messageRepository.saveMessage(conversationId, userMsg)

        // Build system prompt with learnings
        val learnings = learningRepository.getLearnings()
        val systemPrompt = buildSystemPrompt(learnings)

        // Generate response
        var fullResponse = ""
        val assistantMsgId = UUID.randomUUID().toString()

        return llmProvider.generateStream(userMessage, systemPrompt)
            .onStart { fullResponse = "" }
            .onCompletion { error ->
                if (error == null && fullResponse.isNotBlank()) {
                    // Save assistant response
                    val assistantMsg = ChatMessage(
                        id = assistantMsgId,
                        role = MessageRole.ASSISTANT,
                        content = fullResponse.trim()
                    )
                    messageRepository.saveMessage(conversationId, assistantMsg)
                }
            }
            .also { flow ->
                // Collect tokens to build full response
                // Note: This is a simplified approach for POC
            }
    }

    private fun buildSystemPrompt(learnings: com.jarvis.core.model.Learnings): String = buildString {
        appendLine("You are JARVIS, a helpful AI assistant that learns and improves.")
        appendLine()

        if (learnings.preferences.isNotEmpty()) {
            appendLine("## User Preferences (Learned)")
            learnings.preferences.forEach { pref ->
                appendLine("- ${pref.key}: ${pref.value}")
            }
            appendLine()
        }

        if (learnings.corrections.isNotEmpty()) {
            appendLine("## IMPORTANT: Past Corrections (Never repeat these mistakes)")
            learnings.corrections.forEach { correction ->
                appendLine("- ❌ Don't say: \"${correction.originalResponse.take(100)}\"")
                appendLine("  ✅ Say instead: \"${correction.correctedResponse.take(100)}\"")
            }
            appendLine()
        }

        appendLine("Be helpful, concise, and learn from feedback.")
    }
}
