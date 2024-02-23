import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import extensions.clamped
import extensions.onCameraTouchEvent
import focus.FocusTap
import permissions.SharedImage
import state.CamSelector
import state.CameraState
import state.CaptureMode
import state.FlashMode
import state.ImageCaptureMode
import state.ImageTargetSize
import state.ScaleType

@SuppressLint("RestrictedApi")
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
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleEvent by lifecycleOwner.lifecycle.observeAsState()
    val cameraIsInitialized by rememberUpdatedState(cameraState.isInitialized)
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    val isCameraIdle by rememberUpdatedState(!cameraState.isStreaming)
    var latestBitmap by remember { mutableStateOf<Bitmap?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                controller = cameraState.controller.apply {
                    bindToLifecycle(lifecycleOwner)
                }
                previewStreamState.observe(lifecycleOwner) { state ->
                    cameraState.isStreaming = state == PreviewView.StreamState.STREAMING
                }
            }
        },
        update = { previewView ->
            if (cameraIsInitialized) {
                with(previewView) {
                    this.scaleType = scaleType.type
                    onCameraTouchEvent(
                        onTap = {
                            if (isFocusOnTapEnabled) tapOffset = it
                        },
                        onScaleChanged = {
                            if (isPinchToZoomEnabled) {
                                val zoom = zoomRatio.clamped(it).coerceIn(
                                    minimumValue = cameraState.minZoom,
                                    maximumValue = cameraState.maxZoom
                                )
                                onZoomRatioChanged(zoom)
                            }
                        }
                    )
                    latestBitmap = when {
                        lifecycleEvent == Lifecycle.Event.ON_STOP -> null
                        !isCameraIdle && camSelector != cameraState.camSelector -> bitmap
                        else -> latestBitmap
                    }
                    cameraState.update(
                        camSelector = camSelector,
                        captureMode = captureMode,
                        imageCaptureTargetSize = imageCaptureTargetSize,
                        scaleType = scaleType,
                        isImageAnalysisEnabled = isImageAnalysisEnabled,
                        isFocusOnTapEnabled = isFocusOnTapEnabled,
                        flashMode = flashMode,
                        enableTorch = enableTorch,
                        zoomRatio = zoomRatio,
                        imageCaptureMode = imageCaptureMode,
                        exposureCompensation = exposureCompensation,
                    )
                }
            }
        }
    )

    FocusTap(
        offset = tapOffset,
        onFocus = { onFocus { tapOffset = Offset.Zero } },
        focusContent = focusTapContent
    )
    if (isCameraIdle) {
        latestBitmap?.let {
            when (camSelector.selector.lensFacing) {
                CameraSelector.LENS_FACING_FRONT -> onSwipeToFront(SharedImage(it))
                CameraSelector.LENS_FACING_BACK -> onSwipeToBack(SharedImage(it))
                else -> Unit
            }
            LaunchedEffect(latestBitmap) {
                onPreviewStreamChanged()
                if (latestBitmap != null) onZoomRatioChanged(cameraState.minZoom)
            }
        }
    }

    content()
}