package state

import androidx.compose.runtime.Immutable
import extensions.ImageFile

public sealed interface ImageCaptureResult {
    @Immutable
    public data class Success(val imageFile: ImageFile?) : ImageCaptureResult

    @Immutable
    public data class Error(val throwable: Throwable) : ImageCaptureResult
}