package state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import platform.AVFoundation.AVCaptureDevicePosition
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront

actual enum class CamSelector(
    internal val selector: AVCaptureDevicePosition
) {
    Front(AVCaptureDevicePositionFront),
    Back(AVCaptureDevicePositionBack);

    val inverse: CamSelector
        get() = when (this) {
            Front -> Back
            Back -> Front
        }

    internal companion object {

        internal val Saver: Saver<MutableState<CamSelector>, *> = listSaver(
            save = { listOf(it.value.selector) },
            restore = {
                mutableStateOf(
                    when (it[0]) {
                        AVCaptureDevicePositionFront -> Front
                        else -> Back
                    }
                )
            }
        )
    }
}