package io.github.estivensh.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import io.github.estivensh.helper.FileDataSource

expect class CameraState {
    val fileDataSource: FileDataSource
    val controller: CameraController
    internal var flashMode: FlashMode
    var hasFlashUnit: Boolean
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
    internal var isFocusOnTapEnabled: Boolean
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
    fun takePicture(onResult: (ImageCaptureResult) -> Unit)
    fun startRecording(onResult: (VideoCaptureResult) -> Unit)
    fun stopRecording()
    fun pauseRecording()
    fun resumeRecording()
    fun toggleRecording(onResult: (VideoCaptureResult) -> Unit)
    internal fun update(
        camSelector: CamSelector,
        captureMode: CaptureMode,
        scaleType: ScaleType,
        imageCaptureTargetSize: ImageTargetSize?,
        isImageAnalysisEnabled: Boolean,
        isFocusOnTapEnabled: Boolean,
        flashMode: FlashMode,
        zoomRatio: Float,
        imageCaptureMode: ImageCaptureMode,
        enableTorch: Boolean,
        exposureCompensation: Int,
    )

    companion object {
        val INITIAL_ZOOM_VALUE: Float
        val INITIAL_EXPOSURE_VALUE: Int
    }

}

@Composable
expect fun rememberCameraState(): CameraState

@Composable
expect fun rememberCamSelector(selector: CamSelector): MutableState<CamSelector>

@Composable
expect fun CameraState.rememberFlashMode(
    initialFlashMode: FlashMode,
    useSaver: Boolean = true
): MutableState<FlashMode>

@Composable
expect fun CameraState.rememberTorch(
    initialTorch: Boolean = false,
    useSaver: Boolean = true
): MutableState<Boolean>