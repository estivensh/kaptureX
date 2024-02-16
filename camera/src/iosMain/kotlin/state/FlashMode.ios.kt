package state

import platform.AVFoundation.AVCaptureFlashMode
import platform.AVFoundation.AVCaptureFlashModeAuto
import platform.AVFoundation.AVCaptureFlashModeOff
import platform.AVFoundation.AVCaptureFlashModeOn

actual enum class FlashMode(val mode: AVCaptureFlashMode) {
    On(AVCaptureFlashModeOn),
    Auto(AVCaptureFlashModeAuto),
    Off(AVCaptureFlashModeOff);

    val inverse: FlashMode
        get() = when (this) {
            On -> Off
            else -> On
        }

    internal companion object {
        internal fun find(mode: AVCaptureFlashMode) = entries.firstOrNull { it.mode == mode } ?: Off
    }
}