package io.github.estivensh

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.estivensh.camera.BlinkPictureBox
import io.github.estivensh.camera.CameraInnerContent
import io.github.estivensh.camera.model.CameraOption
import io.github.estivensh.camera.model.Flash
import io.github.estivensh.camera.model.toFlash
import io.github.estivensh.camera.model.toFlashMode
import io.github.estivensh.extensions.ImageFile
import io.github.estivensh.state.CamSelector
import io.github.estivensh.state.CameraState
import io.github.estivensh.state.FlashMode
import io.github.estivensh.state.ImageCaptureResult
import io.github.estivensh.state.VideoCaptureResult
import io.github.estivensh.state.rememberTorch

@Stable
@ExperimentalCameraPreview
object CameraPreviewDefaults {

    @Composable
    fun Camera(
        cameraState: CameraState,
        lastPicture: ImageFile?,
        onTakePicture: (ImageCaptureResult) -> Unit,
        onRecording: (VideoCaptureResult) -> Unit,
        onGalleryClick: () -> Unit,
        zoomRatio: Float,
        camSelector: CamSelector,
        camSelectorOnChanged: (CamSelector) -> Unit,
        flashMode: FlashMode,
        flashModeOnChanged: (FlashMode) -> Unit,
        cameraOption: CameraOption,
        cameraOptionOnChanged: (CameraOption) -> Unit
    ) {

        var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
        val hasFlashUnit by rememberUpdatedState(cameraState.hasFlashUnit)
        val isRecording by rememberUpdatedState(cameraState.isRecording)
        var enableTorch by cameraState.rememberTorch(initialTorch = false)

        BlinkPictureBox(lastPicture, cameraOption == CameraOption.Video)
        CameraInnerContent(
            modifier = Modifier.fillMaxSize(),
            zoomHasChanged = zoomHasChanged,
            zoomRatio = zoomRatio,
            flashMode = flashMode.toFlash(enableTorch),
            isRecording = isRecording,
            cameraOption = cameraOption,
            hasFlashUnit = hasFlashUnit,
            isVideoSupported = true,
            onFlashModeChanged = { flash ->
                enableTorch = flash == Flash.Always
                //Napier.d { "Flash: ${flash.toFlashMode()}" }
                flashModeOnChanged(flash.toFlashMode())
            },
            onZoomFinish = { zoomHasChanged = false },
            lastPicture = lastPicture,
            onTakePicture = {
                cameraState.takePicture(
                    onResult = onTakePicture
                )
            },
            onRecording = {
                cameraState.toggleRecording(onRecording)
            },
            onSwitchCamera = {
                /*if (cameraState.isStreaming) {
                    camSelector = camSelector.inverse
                }*/
                camSelectorOnChanged(camSelector.inverse)
            },
            onCameraOptionChanged = cameraOptionOnChanged,
            onGalleryClick = onGalleryClick,
        )
    }
}