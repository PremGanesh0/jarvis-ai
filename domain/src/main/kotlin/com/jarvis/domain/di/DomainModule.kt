package com.jarvis.domain.di

import com.jarvis.domain.usecase.SaveCorrectionUseCase
import com.jarvis.domain.usecase.SendMessageUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { SendMessageUseCase(get(), get(), get()) }
    factory { SaveCorrectionUseCase(get()) }
}
