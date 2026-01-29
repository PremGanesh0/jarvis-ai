package com.jarvis.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jarvis.data.local.entity.CorrectionEntity
import com.jarvis.data.local.entity.MessageEntity
import com.jarvis.data.local.entity.MetricsEntity
import com.jarvis.data.local.entity.PreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getTotalCount(): Int
}

@Dao
interface CorrectionDao {
    @Query("SELECT * FROM corrections ORDER BY timestamp DESC")
    fun getAllCorrections(): Flow<List<CorrectionEntity>>

    @Query("SELECT * FROM corrections ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentCorrections(limit: Int): List<CorrectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(correction: CorrectionEntity)

    @Query("SELECT COUNT(*) FROM corrections")
    suspend fun getTotalCount(): Int
}

@Dao
interface PreferenceDao {
    @Query("SELECT * FROM preferences ORDER BY confidence DESC")
    fun getAllPreferences(): Flow<List<PreferenceEntity>>

    @Query("SELECT * FROM preferences WHERE category = :category")
    suspend fun getByCategory(category: String): List<PreferenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preference: PreferenceEntity)

    @Query("SELECT COUNT(*) FROM preferences")
    suspend fun getTotalCount(): Int
}

@Dao
interface MetricsDao {
    @Query("SELECT * FROM metrics WHERE date = :date")
    suspend fun getByDate(date: String): MetricsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metrics: MetricsEntity)

    @Query("SELECT SUM(totalMessages) FROM metrics")
    suspend fun getTotalMessages(): Int?

    @Query("SELECT SUM(corrections) FROM metrics")
    suspend fun getTotalCorrections(): Int?
}
