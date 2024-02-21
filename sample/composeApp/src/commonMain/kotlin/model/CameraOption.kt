package model

import org.jetbrains.compose.resources.ExperimentalResourceApi
import state.CaptureMode

@OptIn(ExperimentalResourceApi::class)
enum class CameraOption(
    val titleRes: String,
    //val titleRes: StringResource,
) {
    Photo("Res.string.photo"),
    Video("Res.string.video");
}

expect fun CameraOption.toCaptureMode(): CaptureMode
