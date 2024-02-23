package camera.model

import state.FlashMode

actual fun Flash.toFlashMode(): FlashMode {
    return FlashMode.Off
}

actual fun FlashMode.toFlash(isTorchEnabled: Boolean): Flash {
    return Flash.Auto
}