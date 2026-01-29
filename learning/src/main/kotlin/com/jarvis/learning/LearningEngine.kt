package com.jarvis.learning

import com.jarvis.core.model.Correction
import com.jarvis.core.model.LearnedPreference
import com.jarvis.core.model.Learnings
import com.jarvis.core.model.PreferenceCategory
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
        context: String = "",
    ) {
        val correction = Correction(
            id = generateId(),
            originalResponse = originalResponse,
            correctedResponse = correctedResponse,
            context = context,
            timestamp = System.currentTimeMillis(),
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
    ) {
        val preference = LearnedPreference(
            id = generateId(),
            key = key,
            value = value,
            category = category,
            confidence = 0.7f, // Initial confidence
            learnedAt = System.currentTimeMillis(),
        )
        learningRepository.savePreference(preference)
    }

    /**
     * Builds a learning context string to inject into the system prompt.
     */
    suspend fun buildLearningContext(): String {
        val learnings = learningRepository.getLearnings()
        return buildContextFromLearnings(learnings)
    }

    /**
     * Observes learnings as a flow for reactive updates.
     */
    fun observeLearnings(): Flow<String> =
        learningRepository.getCorrectionsFlow().map { corrections ->
            val learnings = Learnings(
                preferences = emptyList(),
                corrections = corrections,
                daysSinceStart = 1,
            )
            buildContextFromLearnings(learnings)
        }

    private fun buildContextFromLearnings(learnings: Learnings): String {
        if (learnings.corrections.isEmpty() && learnings.preferences.isEmpty()) {
            return ""
        }

        val contextBuilder = StringBuilder()
        contextBuilder.appendLine("=== LEARNED FROM USER ===")

        // Add recent corrections
        val recentCorrections = learnings.corrections
            .sortedByDescending { it.timestamp }
            .take(10)

        if (recentCorrections.isNotEmpty()) {
            contextBuilder.appendLine("\n[Corrections to Apply]")
            recentCorrections.forEach { correction ->
                contextBuilder.appendLine("- When I said: \"${correction.originalResponse.take(100)}...\"")
                contextBuilder.appendLine("  User wanted: \"${correction.correctedResponse.take(100)}...\"")
            }
        }

        // Add preferences by category
        val preferencesByCategory = learnings.preferences.groupBy { it.category }

        preferencesByCategory.forEach { (category, prefs) ->
            val categoryName = when (category) {
                PreferenceCategory.COMMUNICATION_STYLE -> "Communication Style"
                PreferenceCategory.PERSONAL_INFO -> "Personal Info"
                PreferenceCategory.TIME_PREFERENCES -> "Time Preferences"
                PreferenceCategory.TOPICS_OF_INTEREST -> "Topics of Interest"
                PreferenceCategory.CORRECTIONS -> "Corrections"
            }
            contextBuilder.appendLine("\n[$categoryName]")
            prefs.sortedByDescending { it.confidence }.take(5).forEach { pref ->
                contextBuilder.appendLine("- ${pref.key}: ${pref.value}")
            }
        }

        contextBuilder.appendLine("=== END LEARNINGS ===")
        return contextBuilder.toString()
    }

    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}
