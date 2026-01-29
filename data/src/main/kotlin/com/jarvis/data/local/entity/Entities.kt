package com.jarvis.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "corrections")
data class CorrectionEntity(
    @PrimaryKey val id: String,
    val originalResponse: String,
    val correctedResponse: String,
    val context: String,
    val timestamp: Long
)

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val id: String,
    val category: String,
    val key: String,
    val value: String,
    val confidence: Float,
    val learnedAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "metrics")
data class MetricsEntity(
    @PrimaryKey val date: String,
    val totalMessages: Int,
    val corrections: Int,
    val preferencesLearned: Int
)
