pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "jarvis-ai"

// App module
include(":app")

// Core modules
include(":core:core-common")
include(":core:core-model")

// Domain layer
include(":domain")

// Data layer
include(":data")

// AI modules (POC: only llama for now)
include(":ai:ai-core")
include(":ai:ai-llama")

// Learning system
include(":learning")

// Feature modules (POC: only chat)
include(":feature:feature-chat")
