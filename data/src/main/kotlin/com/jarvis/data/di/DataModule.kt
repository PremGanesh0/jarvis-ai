package com.jarvis.data.di

import androidx.room.Room
import com.jarvis.data.local.JarvisDatabase
import com.jarvis.data.repository.LearningRepository
import com.jarvis.data.repository.LearningRepositoryImpl
import com.jarvis.data.repository.MessageRepository
import com.jarvis.data.repository.MessageRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            JarvisDatabase::class.java,
            "jarvis.db"
        ).build()
    }

    // DAOs
    single { get<JarvisDatabase>().messageDao() }
    single { get<JarvisDatabase>().correctionDao() }
    single { get<JarvisDatabase>().preferenceDao() }
    single { get<JarvisDatabase>().metricsDao() }

    // Repositories
    single<MessageRepository> { MessageRepositoryImpl(get()) }
    single<LearningRepository> { LearningRepositoryImpl(get(), get()) }
}
