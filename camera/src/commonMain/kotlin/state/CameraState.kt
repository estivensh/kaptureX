package state

import androidx.compose.runtime.Composable

expect class CameraState {
    val controller: CameraController
    internal var flashMode: FlashMode
    internal var hasFlashUnit: Boolean
    val isZoomSupported: Boolean
    var maxZoom: Float
        internal set
    var minZoom: Float
        internal set
    internal var scaleType: ScaleType
    internal var camSelector: CamSelector
    internal var captureMode: CaptureMode
    internal var imageCaptureMode: ImageCaptureMode
    internal var enableTorch: Boolean
    internal var isImageAnalysisEnabled: Boolean
        private set
    val initialExposure: Int
    internal var imageCaptureTargetSize: ImageTargetSize?
    var isImageAnalysisSupported: Boolean
    var isRecording: Boolean
        private set
    var isStreaming: Boolean
        internal set
    var isInitialized: Boolean
        internal set

    fun startZoom()
    fun hasCamera(cameraSelector: CamSelector): Boolean
    fun isImageAnalysisSupported(cameraSelector: CamSelector): Boolean

    companion object {
        val INITIAL_ZOOM_VALUE: Float
        val INITIAL_EXPOSURE_VALUE: Int
    }

}

fun CameraState.update(
    camSelector: CamSelector,
    captureMode: CaptureMode,
    scaleType: ScaleType,
    imageCaptureTargetSize: ImageTargetSize?,
    isImageAnalysisEnabled: Boolean,
    imageAnalyzer: ImageAnalyzer?,
    //implementationMode: ImplementationMode,
    //isFocusOnTapEnabled: Boolean,
    flashMode: FlashMode,
    zoomRatio: Float,
    imageCaptureMode: ImageCaptureMode,
    enableTorch: Boolean,
    exposureCompensation: Int,
   // videoQualitySelector: QualitySelector,
) {
    this.camSelector = camSelector
    this.scaleType = scaleType
    this.flashMode = flashMode
}

@Composable
expect fun rememberCameraState(): CameraState