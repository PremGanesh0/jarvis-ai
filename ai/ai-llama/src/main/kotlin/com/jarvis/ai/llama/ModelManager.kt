package com.jarvis.ai.llama

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manages LLM model files - download, storage, and lifecycle
 */
class ModelManager(
    private val context: Context,
) {
    companion object {
        private const val TAG = "ModelManager"
        private const val MODELS_DIR = "models"

        // Gemma 3 1B for MediaPipe LLM Inference (requires conversion to .task format)
        // For POC: We'll use a pre-converted model from HuggingFace
        const val DEFAULT_MODEL_NAME = "gemma-3-1b-it-int4.task"
        const val DEFAULT_MODEL_URL = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma-3-1b-it-int4.task"
        const val DEFAULT_MODEL_SIZE_BYTES = 750_000_000L // ~750MB estimated
    }

    private val modelsDir: File by lazy {
        File(context.filesDir, MODELS_DIR).apply { mkdirs() }
    }

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    /**
     * Check if the default model is available
     */
    fun isModelAvailable(modelName: String = DEFAULT_MODEL_NAME): Boolean {
        val modelFile = File(modelsDir, modelName)
        return modelFile.exists() && modelFile.length() > 0
    }

    /**
     * Get the path to a model file
     */
    fun getModelPath(modelName: String = DEFAULT_MODEL_NAME): String {
        return File(modelsDir, modelName).absolutePath
    }

    /**
     * Get list of available models
     */
    fun getAvailableModels(): List<ModelFile> {
        return modelsDir.listFiles()
            ?.filter { it.extension == "gguf" }
            ?.map { ModelFile(it.name, it.absolutePath, it.length()) }
            ?: emptyList()
    }

    /**
     * Download a model from URL
     * Returns a flow of download progress (0.0 to 1.0)
     */
    fun downloadModel(
        url: String = DEFAULT_MODEL_URL,
        fileName: String = DEFAULT_MODEL_NAME,
    ): Flow<DownloadState> = flow {
        _isDownloading.value = true
        _downloadProgress.value = 0f
        emit(DownloadState.Starting)

        val targetFile = File(modelsDir, fileName)
        val tempFile = File(modelsDir, "$fileName.tmp")

        try {
            Log.i(TAG, "Starting download: $url")

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 30_000
            connection.readTimeout = 60_000

            val totalSize = connection.contentLengthLong
            var downloadedSize = 0L

            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead

                        val progress = if (totalSize > 0) {
                            downloadedSize.toFloat() / totalSize
                        } else {
                            downloadedSize.toFloat() / DEFAULT_MODEL_SIZE_BYTES
                        }

                        _downloadProgress.value = progress
                        emit(DownloadState.Progress(progress, downloadedSize, totalSize))
                    }
                }
            }

            // Rename temp file to final
            tempFile.renameTo(targetFile)

            _downloadProgress.value = 1f
            _isDownloading.value = false
            emit(DownloadState.Completed(targetFile.absolutePath))

            Log.i(TAG, "Download completed: ${targetFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            tempFile.delete()
            _isDownloading.value = false
            _downloadProgress.value = 0f
            emit(DownloadState.Failed(e.message ?: "Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Delete a model file
     */
    suspend fun deleteModel(modelName: String): Boolean = withContext(Dispatchers.IO) {
        val modelFile = File(modelsDir, modelName)
        if (modelFile.exists()) {
            modelFile.delete()
        } else {
            false
        }
    }

    /**
     * Get total storage used by models
     */
    fun getStorageUsed(): Long {
        return modelsDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
}

data class ModelFile(
    val name: String,
    val path: String,
    val sizeBytes: Long,
)

sealed interface DownloadState {
    data object Starting : DownloadState
    data class Progress(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : DownloadState
    data class Completed(val filePath: String) : DownloadState
    data class Failed(val error: String) : DownloadState
}
