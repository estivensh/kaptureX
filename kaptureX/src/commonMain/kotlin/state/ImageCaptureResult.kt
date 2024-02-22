package state

import androidx.compose.runtime.Immutable
import extensions.ImageFile

sealed interface ImageCaptureResult {
    @Immutable
    data class Success(val imageFile: ImageFile) : ImageCaptureResult

    @Immutable
    data class Error(val throwable: Throwable) : ImageCaptureResult
}