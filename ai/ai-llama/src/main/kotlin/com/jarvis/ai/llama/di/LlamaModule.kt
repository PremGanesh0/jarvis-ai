package com.jarvis.ai.llama.di

import com.jarvis.ai.core.LLMProvider
import com.jarvis.ai.llama.FakeLLMProvider
import org.koin.dsl.module

val llamaModule = module {
    // POC: Use FakeLLMProvider
    // TODO: Replace with real LlamaLLMProvider when native integration is ready
    single<LLMProvider> { FakeLLMProvider() }
}
