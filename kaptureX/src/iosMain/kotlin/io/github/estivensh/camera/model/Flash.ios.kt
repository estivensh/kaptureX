package io.github.estivensh.camera.model

import io.github.estivensh.state.FlashMode

actual fun Flash.toFlashMode(): FlashMode {
    return FlashMode.Off
}

actual fun FlashMode.toFlash(isTorchEnabled: Boolean): Flash {
    return Flash.Auto
}