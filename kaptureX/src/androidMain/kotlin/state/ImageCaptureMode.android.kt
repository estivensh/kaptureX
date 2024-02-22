package state

import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture

actual enum class ImageCaptureMode(
    @ImageCapture.CaptureMode internal val mode: Int
) {
    @ExperimentalZeroShutterLag
    ZeroShutterLag(ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG),
    MaxQuality(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY),
    MinLatency(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY);
}