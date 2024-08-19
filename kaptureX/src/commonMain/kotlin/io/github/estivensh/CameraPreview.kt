package io.github.estivensh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.estivensh.camera.model.CameraOption
import io.github.estivensh.extensions.ImageFile
import io.github.estivensh.focus.SquareCornerFocus
import kotlinx.coroutines.delay
import io.github.estivensh.permissions.SharedImage
import io.github.estivensh.state.CamSelector
import io.github.estivensh.state.CameraState
import io.github.estivensh.state.CaptureMode
import io.github.estivensh.state.FlashMode
import io.github.estivensh.state.ImageCaptureMode
import io.github.estivensh.state.ImageCaptureResult
import io.github.estivensh.state.ImageTargetSize
import io.github.estivensh.state.ScaleType
import io.github.estivensh.state.VideoCaptureResult
import io.github.estivensh.state.rememberCameraState

@ExperimentalCameraPreview
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    camSelector: CamSelector = cameraState.camSelector,
    camSelectorOnChanged: (CamSelector) -> Unit,
    captureMode: CaptureMode = cameraState.captureMode,
    imageCaptureMode: ImageCaptureMode = cameraState.imageCaptureMode,
    imageCaptureTargetSize: ImageTargetSize? = cameraState.imageCaptureTargetSize,
    flashMode: FlashMode = cameraState.flashMode,
    flashModeOnChanged: (FlashMode) -> Unit,
    cameraOption: CameraOption,
    cameraOptionOnChanged: (CameraOption) -> Unit,
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
    content: @Composable () -> Unit = {
        var lastPicture by remember { mutableStateOf<ImageFile?>(null) }

        LaunchedEffect(true) {
            //lastPicture = cameraState.fileDataSource.lastPicture
        }

        CameraPreviewDefaults.Camera(
            cameraState = cameraState,
            camSelector = camSelector,
            camSelectorOnChanged = camSelectorOnChanged,
            zoomRatio = zoomRatio,
            lastPicture = lastPicture,
            onTakePicture = { imageCaptureResult ->
                when (imageCaptureResult) {
                    is ImageCaptureResult.Error -> {} //Napier.e { imageCaptureResult.throwable.message.orEmpty() }
                    is ImageCaptureResult.Success -> lastPicture = imageCaptureResult.imageFile
                }
            },
            onRecording = {
                when (it) {
                    is VideoCaptureResult.Error -> {} //Napier.e { it.message }
                    is VideoCaptureResult.Success -> lastPicture = it.imageFile
                }
            },
            onGalleryClick = {
                //Napier.i { "Gallery click" }
            },
            flashMode = flashMode,
            flashModeOnChanged = flashModeOnChanged,
            cameraOption = cameraOption,
            cameraOptionOnChanged = cameraOptionOnChanged
        )
    },
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