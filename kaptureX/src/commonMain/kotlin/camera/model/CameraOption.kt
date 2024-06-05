package camera.model

import org.jetbrains.compose.resources.ExperimentalResourceApi
import state.CaptureMode

@OptIn(ExperimentalResourceApi::class)
enum class CameraOption(
    val titleRes: String,
) {
    Photo("photo"),
    Video("video");
}

expect fun CameraOption.toCaptureMode(): CaptureMode
