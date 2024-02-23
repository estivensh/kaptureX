package camera.model

import state.CaptureMode

actual fun CameraOption.toCaptureMode(): CaptureMode = CaptureMode.Video