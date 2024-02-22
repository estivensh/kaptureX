package helper

import android.hardware.camera2.CameraCharacteristics
import android.os.Build

internal object CameraHelper {

    private const val COMPAT_HARDWARE_LEVEL_3 = 3

    internal val compatHardwareLevel3: Int = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
}