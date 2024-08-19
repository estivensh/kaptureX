package io.github.estivensh.state

import androidx.compose.runtime.Immutable
import io.github.estivensh.extensions.ImageFile

sealed interface VideoCaptureResult {
    @Immutable
    data class Success(val imageFile: ImageFile) : VideoCaptureResult

    @Immutable
    data class Error(
        val message: String,
        val throwable: Throwable?
    ) : VideoCaptureResult
}