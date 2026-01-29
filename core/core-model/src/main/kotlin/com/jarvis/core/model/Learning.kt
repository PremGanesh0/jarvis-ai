package com.jarvis.core.model

import kotlinx.serialization.Serializable

/**
 * A correction made by the user to a JARVIS response
 */
@Serializable
data class Correction(
    val id: String,
    val originalResponse: String,
    val correctedResponse: String,
    val context: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * A learned preference from conversations
 */
@Serializable
data class LearnedPreference(
    val id: String,
    val category: PreferenceCategory,
    val key: String,
    val value: String,
    val confidence: Float,
    val learnedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class PreferenceCategory {
    COMMUNICATION_STYLE,
    PERSONAL_INFO,
    TIME_PREFERENCES,
    TOPICS_OF_INTEREST,
    CORRECTIONS
}

/**
 * Aggregated learnings for context enrichment
 */
data class Learnings(
    val preferences: List<LearnedPreference>,
    val corrections: List<Correction>,
    val daysSinceStart: Int
)
