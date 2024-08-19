package io.github.estivensh.extensions

actual class CameraManager

internal actual fun CameraManager.isImageAnalysisSupported(lensFacing: Int?): Boolean {
    return true
}