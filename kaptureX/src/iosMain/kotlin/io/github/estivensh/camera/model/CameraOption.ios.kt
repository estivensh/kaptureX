package io.github.estivensh.camera.model

import io.github.estivensh.state.CaptureMode

actual fun CameraOption.toCaptureMode(): CaptureMode = CaptureMode.Video