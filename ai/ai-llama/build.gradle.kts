plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.jarvis.ai.llama"
    compileSdk = 35

    defaultConfig {
        minSdk = 29

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-model"))
    implementation(project(":ai:ai-core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.koin.android)

    // MediaPipe LLM Inference API - Official Google Android LLM support
    implementation("com.google.mediapipe:tasks-genai:0.10.27")
}
