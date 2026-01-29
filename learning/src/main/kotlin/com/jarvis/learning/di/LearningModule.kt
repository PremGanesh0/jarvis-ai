package com.jarvis.learning.di

import com.jarvis.learning.LearningEngine
import org.koin.dsl.module

val learningModule = module {
    single { LearningEngine(get()) }
}
