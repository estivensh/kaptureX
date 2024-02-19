package model

import state.CaptureMode

actual fun CameraOption.toCaptureMode(): CaptureMode {
    return CaptureMode.Video
}