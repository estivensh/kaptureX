package camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import camera.model.CameraOption
import camera.model.Flash
import extensions.ImageFile
import extensions.noClickable

@Composable
fun CameraInnerContent(
    modifier: Modifier = Modifier,
    zoomHasChanged: Boolean,
    zoomRatio: Float,
    flashMode: Flash,
    isRecording: Boolean,
    cameraOption: CameraOption,
    hasFlashUnit: Boolean,
    lastPicture: ImageFile?,
    isVideoSupported: Boolean,
    onGalleryClick: () -> Unit,
    onFlashModeChanged: (Flash) -> Unit,
    onZoomFinish: () -> Unit,
    onRecording: () -> Unit,
    onTakePicture: () -> Unit,
    onSwitchCamera: () -> Unit,
    onCameraOptionChanged: (CameraOption) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SettingsBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp),
            flashMode = flashMode,
            zoomRatio = zoomRatio,
            isVideo = cameraOption == CameraOption.Video,
            hasFlashUnit = hasFlashUnit,
            zoomHasChanged = zoomHasChanged,
            isRecording = isRecording,
            onFlashModeChanged = onFlashModeChanged,
            onZoomFinish = onZoomFinish,
        )
        ActionBox(
            modifier = Modifier
                .fillMaxWidth()
                .noClickable()
                .padding(bottom = 32.dp, top = 16.dp),
            lastPicture = lastPicture,
            onGalleryClick = onGalleryClick,
            cameraOption = cameraOption,
            onTakePicture = onTakePicture,
            isRecording = isRecording,
            isVideoSupported = isVideoSupported,
            onRecording = onRecording,
            onSwitchCamera = onSwitchCamera,
            onCameraOptionChanged = onCameraOptionChanged,
        )
    }
}