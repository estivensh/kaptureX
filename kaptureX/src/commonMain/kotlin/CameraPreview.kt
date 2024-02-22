import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import focus.SquareCornerFocus
import kotlinx.coroutines.delay
import state.CamSelector
import state.CameraState
import state.CaptureMode
import state.FlashMode
import state.ImageCaptureMode
import state.ImageTargetSize
import state.ScaleType
import state.rememberCameraState

@ExperimentalCameraView
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    camSelector: CamSelector = cameraState.camSelector,
    captureMode: CaptureMode = cameraState.captureMode,
    imageCaptureMode: ImageCaptureMode = cameraState.imageCaptureMode,
    imageCaptureTargetSize: ImageTargetSize? = cameraState.imageCaptureTargetSize,
    flashMode: FlashMode = cameraState.flashMode,
    scaleType: ScaleType = cameraState.scaleType,
    enableTorch: Boolean = cameraState.enableTorch,
    exposureCompensation: Int = cameraState.initialExposure,
    zoomRatio: Float = 1F,
    //imageAnalyzer: ImageAnalyzer? = null,
    //implementationMode: ImplementationMode = cameraState.implementationMode,
    isImageAnalysisEnabled: Boolean = cameraState.isImageAnalysisEnabled,
    //isFocusOnTapEnabled: Boolean = cameraState.isFocusOnTapEnabled,
    isPinchToZoomEnabled: Boolean = cameraState.isZoomSupported,
    //videoQualitySelector: QualitySelector = cameraState.videoQualitySelector,
    onPreviewStreamChanged: () -> Unit = {},
    /*  onSwitchToFront: @Composable (Bitmap) -> Unit = {},
      onSwitchToBack: @Composable (Bitmap) -> Unit = {},*/
    onFocus: suspend (onComplete: () -> Unit) -> Unit = { onComplete ->
        delay(1000L)
        onComplete()
    },
    onZoomRatioChanged: (Float) -> Unit = {},
    focusTapContent: @Composable () -> Unit = { SquareCornerFocus() },
    content: @Composable () -> Unit = {},
) {
    CameraPreviewImpl(
        modifier = modifier,
        cameraState = cameraState,
        camSelector = camSelector,
        captureMode = captureMode,
        exposureCompensation = exposureCompensation,
        imageCaptureMode = imageCaptureMode,
        imageCaptureTargetSize = imageCaptureTargetSize,
        flashMode = flashMode,
        scaleType = scaleType,
        enableTorch = enableTorch,
        zoomRatio = zoomRatio,
        //imageAnalyzer = imageAnalyzer,
        isImageAnalysisEnabled = isImageAnalysisEnabled,
        // implementationMode = implementationMode,
        //isFocusOnTapEnabled = isFocusOnTapEnabled,
        isPinchToZoomEnabled = isPinchToZoomEnabled,
        //videoQualitySelector = videoQualitySelector,
        onZoomRatioChanged = onZoomRatioChanged,
        focusTapContent = focusTapContent,
        onFocus = onFocus,
        onPreviewStreamChanged = onPreviewStreamChanged,
        /*onSwipeToFront = onSwitchToFront,
        onSwipeToBack = onSwitchToBack,*/
        content = content
    )
}

@Composable
expect fun CameraPreviewImpl(
    modifier: Modifier,
    cameraState: CameraState,
    camSelector: CamSelector,
    captureMode: CaptureMode,
    imageCaptureMode: ImageCaptureMode,
    imageCaptureTargetSize: ImageTargetSize?,
    flashMode: FlashMode,
    scaleType: ScaleType,
    enableTorch: Boolean,
    zoomRatio: Float,
    //implementationMode: ImplementationMode,
    //imageAnalyzer: ImageAnalyzer?,
    exposureCompensation: Int,
    isImageAnalysisEnabled: Boolean,
    //isFocusOnTapEnabled: Boolean,
    isPinchToZoomEnabled: Boolean,
    //videoQualitySelector: QualitySelector,
    onZoomRatioChanged: (Float) -> Unit,
    onPreviewStreamChanged: () -> Unit,
    onFocus: suspend (() -> Unit) -> Unit,
    focusTapContent: @Composable () -> Unit,
    content: @Composable () -> Unit
)