package com.jarvis.domain.usecase

import com.jarvis.core.model.Correction
import com.jarvis.data.repository.LearningRepository
import java.util.UUID

/**
 * Use case for saving a user correction
 */
class SaveCorrectionUseCase(
    private val learningRepository: LearningRepository
) {
    suspend operator fun invoke(
        originalResponse: String,
        correctedResponse: String,
        context: String = ""
    ) {
        val correction = Correction(
            id = UUID.randomUUID().toString(),
            originalResponse = originalResponse,
            correctedResponse = correctedResponse,
            context = context
        )
        learningRepository.saveCorrection(correction)
    }
}
