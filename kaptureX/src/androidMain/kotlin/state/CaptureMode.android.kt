package state

import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.CameraController.VIDEO_CAPTURE

actual enum class CaptureMode(
    val value: Int
) {
    Image(IMAGE_CAPTURE),
    Video(VIDEO_CAPTURE),
}