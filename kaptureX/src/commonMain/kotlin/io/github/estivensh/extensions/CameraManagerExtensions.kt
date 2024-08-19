package io.github.estivensh.extensions

expect class CameraManager

internal expect fun CameraManager.isImageAnalysisSupported(lensFacing: Int?): Boolean