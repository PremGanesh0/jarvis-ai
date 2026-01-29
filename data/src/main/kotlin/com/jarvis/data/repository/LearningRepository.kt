package com.jarvis.data.repository

import com.jarvis.core.model.Correction
import com.jarvis.core.model.LearnedPreference
import com.jarvis.core.model.Learnings
import com.jarvis.core.model.PreferenceCategory
import com.jarvis.data.local.dao.CorrectionDao
import com.jarvis.data.local.dao.PreferenceDao
import com.jarvis.data.local.entity.CorrectionEntity
import com.jarvis.data.local.entity.PreferenceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LearningRepository {
    suspend fun saveCorrection(correction: Correction)
    suspend fun savePreference(preference: LearnedPreference)
    suspend fun getLearnings(correctionLimit: Int = 10): Learnings
    fun getCorrectionsFlow(): Flow<List<Correction>>
    fun getPreferencesFlow(): Flow<List<LearnedPreference>>
    suspend fun getCorrectionCount(): Int
    suspend fun getPreferenceCount(): Int
}

class LearningRepositoryImpl(
    private val correctionDao: CorrectionDao,
    private val preferenceDao: PreferenceDao
) : LearningRepository {

    override suspend fun saveCorrection(correction: Correction) {
        correctionDao.insert(correction.toEntity())
    }

    override suspend fun savePreference(preference: LearnedPreference) {
        preferenceDao.insert(preference.toEntity())
    }

    override suspend fun getLearnings(correctionLimit: Int): Learnings {
        val corrections = correctionDao.getRecentCorrections(correctionLimit)
            .map { it.toDomain() }
        val preferences = preferenceDao.getAllPreferences()

        // Calculate days since first use (simplified for POC)
        val daysSinceStart = 1 // TODO: Calculate from first message date

        return Learnings(
            preferences = emptyList(), // TODO: collect from flow
            corrections = corrections,
            daysSinceStart = daysSinceStart
        )
    }

    override fun getCorrectionsFlow(): Flow<List<Correction>> {
        return correctionDao.getAllCorrections().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPreferencesFlow(): Flow<List<LearnedPreference>> {
        return preferenceDao.getAllPreferences().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCorrectionCount(): Int {
        return correctionDao.getTotalCount()
    }

    override suspend fun getPreferenceCount(): Int {
        return preferenceDao.getTotalCount()
    }

    // Entity <-> Domain mappers
    private fun CorrectionEntity.toDomain() = Correction(
        id = id,
        originalResponse = originalResponse,
        correctedResponse = correctedResponse,
        context = context,
        timestamp = timestamp
    )

    private fun Correction.toEntity() = CorrectionEntity(
        id = id,
        originalResponse = originalResponse,
        correctedResponse = correctedResponse,
        context = context,
        timestamp = timestamp
    )

    private fun PreferenceEntity.toDomain() = LearnedPreference(
        id = id,
        category = PreferenceCategory.valueOf(category),
        key = key,
        value = value,
        confidence = confidence,
        learnedAt = learnedAt
    )

    private fun LearnedPreference.toEntity() = PreferenceEntity(
        id = id,
        category = category.name,
        key = key,
        value = value,
        confidence = confidence,
        learnedAt = learnedAt,
        updatedAt = System.currentTimeMillis()
    )
}
