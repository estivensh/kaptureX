package io.github.estivensh.camera.model

import io.github.estivensh.state.FlashMode

actual fun Flash.toFlashMode(): FlashMode = when (this) {
    Flash.Auto -> FlashMode.Auto
    Flash.On -> FlashMode.On
    Flash.Off, Flash.Always -> FlashMode.Off
}


actual fun FlashMode.toFlash(isTorchEnabled: Boolean) = when (this) {
    FlashMode.On -> Flash.On
    FlashMode.Auto -> Flash.Auto
    FlashMode.Off -> Flash.Off
}.takeIf { !isTorchEnabled } ?: Flash.Always