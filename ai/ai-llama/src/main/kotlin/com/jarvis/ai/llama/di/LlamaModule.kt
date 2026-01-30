package com.jarvis.ai.llama.di

import com.jarvis.ai.core.LLMProvider
import com.jarvis.ai.llama.FakeLLMProvider
import com.jarvis.ai.llama.MediaPipeLLMProvider
import com.jarvis.ai.llama.ModelManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Set to true to use real LLM, false for fake POC responses
 * Note: Real LLM (MediaPipe) doesn't work on emulators - needs physical device
 */
const val USE_REAL_LLM = false

val llamaModule = module {
    // Expose the USE_REAL_LLM flag for other modules
    single(named("useRealLLM")) { USE_REAL_LLM }

    // Model manager for downloading/managing models
    single { ModelManager(androidContext()) }

    // LLM Provider - switch between real and fake
    single<LLMProvider> {
        if (USE_REAL_LLM) {
            MediaPipeLLMProvider(androidContext())
        } else {
            FakeLLMProvider()
        }
    }
}
