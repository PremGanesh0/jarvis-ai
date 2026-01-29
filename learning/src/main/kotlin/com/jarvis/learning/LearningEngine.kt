package com.jarvis.learning

import com.jarvis.coremodel.Correction
import com.jarvis.coremodel.LearnedPreference
import com.jarvis.coremodel.Learnings
import com.jarvis.coremodel.PreferenceCategory
import com.jarvis.data.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Core learning engine that processes corrections and builds context
 * for the AI to improve over time.
 *
 * POC: Simple implementation that stores corrections and preferences.
 * Production: Will include pattern recognition, clustering, and priority scoring.
 */
class LearningEngine(
    private val learningRepository: LearningRepository,
) {
    /**
     * Records a user correction for a previous AI response.
     */
    suspend fun recordCorrection(
        originalResponse: String,
        correctedResponse: String,
        conversationId: String,
        messageId: String,
    ) {
        val correction = Correction(
            id = generateId(),
            originalResponse = originalResponse,
            correctedResponse = correctedResponse,
            timestamp = System.currentTimeMillis(),
            category = inferCategory(originalResponse, correctedResponse),
            priority = calculatePriority(originalResponse, correctedResponse),
            conversationId = conversationId,
            messageId = messageId,
        )
        learningRepository.saveCorrection(correction)
    }

    /**
     * Records a learned user preference.
     */
    suspend fun recordPreference(
        key: String,
        value: String,
        category: PreferenceCategory,
        source: String,
    ) {
        val preference = LearnedPreference(
            id = generateId(),
            key = key,
            value = value,
            category = category,
            confidence = 0.7f, // Initial confidence
            learnedAt = System.currentTimeMillis(),
            source = source,
        )
        learningRepository.savePreference(preference)
    }

    /**
     * Builds a learning context string to inject into the system prompt.
     */
    suspend fun buildLearningContext(): String {
        val learnings = learningRepository.getAllLearnings()
        return buildContextFromLearnings(learnings)
    }

    /**
     * Observes learnings as a flow for reactive updates.
     */
    fun observeLearnings(): Flow<String> =
        learningRepository.observeLearnings().map { buildContextFromLearnings(it) }

    private fun buildContextFromLearnings(learnings: Learnings): String {
        if (learnings.corrections.isEmpty() && learnings.preferences.isEmpty()) {
            return ""
        }

        val contextBuilder = StringBuilder()
        contextBuilder.appendLine("=== LEARNED FROM USER ===")

        // Add high-priority corrections
        val importantCorrections = learnings.corrections
            .filter { it.priority >= 0.7f }
            .sortedByDescending { it.priority }
            .take(10)

        if (importantCorrections.isNotEmpty()) {
            contextBuilder.appendLine("\n[Corrections to Apply]")
            importantCorrections.forEach { correction ->
                contextBuilder.appendLine("- When I said: \"${correction.originalResponse.take(100)}...\"")
                contextBuilder.appendLine("  User wanted: \"${correction.correctedResponse.take(100)}...\"")
            }
        }

        // Add preferences by category
        val preferencesByCategory = learnings.preferences.groupBy { it.category }

        preferencesByCategory.forEach { (category, prefs) ->
            contextBuilder.appendLine("\n[${category.displayName}]")
            prefs.sortedByDescending { it.confidence }.take(5).forEach { pref ->
                contextBuilder.appendLine("- ${pref.key}: ${pref.value}")
            }
        }

        contextBuilder.appendLine("=== END LEARNINGS ===")
        return contextBuilder.toString()
    }

    private fun inferCategory(original: String, corrected: String): String {
        // POC: Simple keyword-based categorization
        val lowerCorrected = corrected.lowercase()
        return when {
            lowerCorrected.contains("formal") || lowerCorrected.contains("casual") -> "tone"
            lowerCorrected.contains("shorter") || lowerCorrected.contains("longer") -> "length"
            lowerCorrected.contains("technical") || lowerCorrected.contains("simple") -> "complexity"
            else -> "general"
        }
    }

    private fun calculatePriority(original: String, corrected: String): Float {
        // POC: All corrections start with high priority
        // Production: Will analyze frequency, recency, and impact
        return 0.8f
    }

    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}
