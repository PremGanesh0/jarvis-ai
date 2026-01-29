package com.jarvis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jarvis.data.local.dao.CorrectionDao
import com.jarvis.data.local.dao.MessageDao
import com.jarvis.data.local.dao.MetricsDao
import com.jarvis.data.local.dao.PreferenceDao
import com.jarvis.data.local.entity.CorrectionEntity
import com.jarvis.data.local.entity.MessageEntity
import com.jarvis.data.local.entity.MetricsEntity
import com.jarvis.data.local.entity.PreferenceEntity

@Database(
    entities = [
        MessageEntity::class,
        CorrectionEntity::class,
        PreferenceEntity::class,
        MetricsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun correctionDao(): CorrectionDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun metricsDao(): MetricsDao
}
