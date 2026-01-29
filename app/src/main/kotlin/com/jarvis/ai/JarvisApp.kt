package com.jarvis.ai

import android.app.Application
import com.jarvis.ai.di.appModule
import com.jarvis.data.di.dataModule
import com.jarvis.domain.di.domainModule
import com.jarvis.ai.llama.di.llamaModule
import com.jarvis.learning.di.learningModule
import com.jarvis.feature.chat.di.chatModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class JarvisApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@JarvisApp)
            modules(
                appModule,
                dataModule,
                domainModule,
                llamaModule,
                learningModule,
                chatModule
            )
        }
    }
}
