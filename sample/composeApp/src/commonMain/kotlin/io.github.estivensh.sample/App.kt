package io.github.estivensh.sample

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.github.estivensh.CameraPreview
import io.github.estivensh.ExperimentalCameraPreview
import io.github.estivensh.camera.model.CameraOption
import io.github.estivensh.camera.model.toCaptureMode
/*import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.SwipeProperties
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition*/
import io.github.estivensh.state.CamSelector
import io.github.estivensh.state.CameraState
import io.github.estivensh.state.FlashMode
import io.github.estivensh.state.rememberCamSelector
import io.github.estivensh.state.rememberCameraState
import io.github.estivensh.state.rememberFlashMode

@Composable
fun App() {
     val cameraState = rememberCameraState()
    CameraSection(
        cameraState = cameraState,
        useFrontCamera = false,
        usePinchToZoom = true
    )
}

@OptIn(ExperimentalCameraPreview::class)
@Composable
fun CameraSection(
    cameraState: CameraState,
    useFrontCamera: Boolean,
    usePinchToZoom: Boolean
) {
    var flashMode by cameraState.rememberFlashMode(FlashMode.valueOf("On"))
    var camSelector by rememberCamSelector(if (useFrontCamera) CamSelector.Front else CamSelector.Back)
    var zoomRatio by rememberSaveable { mutableStateOf(cameraState.minZoom) }
    var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
    var cameraOption by rememberSaveable { mutableStateOf(CameraOption.Video) }
    val enableTorch = true
    CameraPreview(
        cameraState = cameraState,
        camSelector = camSelector,
        captureMode = cameraOption.toCaptureMode(),
        flashMode = flashMode,
        enableTorch = enableTorch,
        zoomRatio = zoomRatio,
        isPinchToZoomEnabled = usePinchToZoom,
        onZoomRatioChanged = {
            zoomHasChanged = true
            zoomRatio = it
        },
        onSwitchToFront = { _ ->

        },
        onSwitchToBack = { _ ->

        },
        camSelectorOnChanged = { camSelector = it },
        flashModeOnChanged = { flashMode = it },
        cameraOptionOnChanged = { cameraOption = it },
        cameraOption = cameraOption,
    )
}
