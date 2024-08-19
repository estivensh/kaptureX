package io.github.estivensh.camera.model

import io.github.estivensh.state.CaptureMode

actual fun CameraOption.toCaptureMode(): CaptureMode = when (this) {
    CameraOption.Photo -> CaptureMode.Image
    CameraOption.Video -> CaptureMode.Video
}