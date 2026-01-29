package com.jarvis.feature.chat.di

import com.jarvis.feature.chat.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    viewModel { ChatViewModel(get(), get(), get()) }
}
