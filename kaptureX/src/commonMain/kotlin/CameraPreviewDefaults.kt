import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import camera.BlinkPictureBox
import camera.CameraInnerContent
import camera.model.CameraOption
import camera.model.Flash
import camera.model.toFlash
import camera.model.toFlashMode
import extensions.ImageFile
import state.CamSelector
import state.CameraState
import state.FlashMode
import state.ImageCaptureResult
import state.VideoCaptureResult
import state.rememberTorch

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