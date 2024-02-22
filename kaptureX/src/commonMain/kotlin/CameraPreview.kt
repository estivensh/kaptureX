import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import focus.SquareCornerFocus
import kotlinx.coroutines.delay
import permissions.SharedImage
import state.CamSelector
import state.CameraState
import state.CaptureMode
import state.FlashMode
import state.ImageCaptureMode
import state.ImageTargetSize
import state.ScaleType
import state.rememberCameraState

@ExperimentalCameraPreview
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
    isImageAnalysisEnabled: Boolean = cameraState.isImageAnalysisEnabled,
    isFocusOnTapEnabled: Boolean = cameraState.isFocusOnTapEnabled,
    isPinchToZoomEnabled: Boolean = cameraState.isZoomSupported,
    onPreviewStreamChanged: () -> Unit = {},
    onSwitchToFront: @Composable (SharedImage) -> Unit = {},
    onSwitchToBack: @Composable (SharedImage) -> Unit = {},
    onFocus: suspend (onComplete: () -> Unit) -> Unit = { onComplete ->
        delay(1000L)
        onComplete()
    },
    onZoomRatioChanged: (Float) -> Unit = {},
    focusTapContent: @Composable () -> Unit = { SquareCornerFocus() },
    content: @Composable () -> Unit = { CameraPreviewDefaults.Camera() },
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
        isImageAnalysisEnabled = isImageAnalysisEnabled,
        isFocusOnTapEnabled = isFocusOnTapEnabled,
        isPinchToZoomEnabled = isPinchToZoomEnabled,
        onZoomRatioChanged = onZoomRatioChanged,
        focusTapContent = focusTapContent,
        onFocus = onFocus,
        onPreviewStreamChanged = onPreviewStreamChanged,
        onSwipeToFront = onSwitchToFront,
        onSwipeToBack = onSwitchToBack,
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
    exposureCompensation: Int,
    isImageAnalysisEnabled: Boolean,
    isFocusOnTapEnabled: Boolean,
    isPinchToZoomEnabled: Boolean,
    onZoomRatioChanged: (Float) -> Unit,
    onPreviewStreamChanged: () -> Unit,
    onFocus: suspend (() -> Unit) -> Unit,
    onSwipeToFront: @Composable (SharedImage) -> Unit,
    onSwipeToBack: @Composable (SharedImage) -> Unit,
    focusTapContent: @Composable () -> Unit,
    content: @Composable () -> Unit
)