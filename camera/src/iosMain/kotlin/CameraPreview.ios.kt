import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.skia.Bitmap
import state.CamSelector
import state.CameraState
import state.CaptureMode
import state.FlashMode
import state.ImageAnalyzer
import state.ImageCaptureMode
import state.ImageTargetSize
import state.ImplementationMode
import state.QualitySelector
import state.ScaleType

@Composable
actual fun CameraPreviewImpl(
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
    imageAnalyzer: ImageAnalyzer?,
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
) {
}