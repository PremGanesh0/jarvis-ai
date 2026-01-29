package com.jarvis.core.model

import kotlinx.serialization.Serializable

/**
 * Represents a chat message in a conversation
 */
@Serializable
data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}
