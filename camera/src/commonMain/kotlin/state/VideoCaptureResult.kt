package state

import androidx.compose.runtime.Immutable
import extensions.ImageFile

sealed interface VideoCaptureResult {
    @Immutable
    data class Success(val imageFile: ImageFile) : VideoCaptureResult

    @Immutable
    data class Error(
        val message: String,
        val throwable: Throwable?
    ) : VideoCaptureResult
}