package io.github.estivensh.state

import androidx.compose.runtime.Immutable
import io.github.estivensh.extensions.ImageFile

sealed interface ImageCaptureResult {
    @Immutable
    data class Success(val imageFile: ImageFile) : ImageCaptureResult

    @Immutable
    data class Error(val throwable: Throwable) : ImageCaptureResult
}