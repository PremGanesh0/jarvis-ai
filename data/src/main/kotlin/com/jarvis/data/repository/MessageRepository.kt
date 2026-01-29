package com.jarvis.data.repository

import com.jarvis.core.model.ChatMessage
import com.jarvis.core.model.MessageRole
import com.jarvis.data.local.dao.MessageDao
import com.jarvis.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MessageRepository {
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun saveMessage(conversationId: String, message: ChatMessage)
    suspend fun getTotalCount(): Int
}

class MessageRepositoryImpl(
    private val messageDao: MessageDao
) : MessageRepository {

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> {
        return messageDao.getMessages(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveMessage(conversationId: String, message: ChatMessage) {
        messageDao.insert(message.toEntity(conversationId))
    }

    override suspend fun getTotalCount(): Int {
        return messageDao.getTotalCount()
    }

    private fun MessageEntity.toDomain() = ChatMessage(
        id = id,
        role = MessageRole.valueOf(role),
        content = content,
        timestamp = timestamp
    )

    private fun ChatMessage.toEntity(conversationId: String) = MessageEntity(
        id = id,
        conversationId = conversationId,
        role = role.name,
        content = content,
        timestamp = timestamp
    )
}
