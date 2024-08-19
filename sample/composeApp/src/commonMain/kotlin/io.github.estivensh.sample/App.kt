package io.github.estivensh.sample

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
    CameraScreen()
    /*PreComposeApp {
        MaterialTheme {
            val navigator = rememberNavigator()
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = Router.Camera.route
            ) {
                newScene(Router.Camera) {
                    CameraScreen()
                }
                newScene(Router.Gallery) {
                    GalleryScreen(
                        onBackPressed = {
                            navigator.popBackStack()
                        },
                        onPreviewClick = {

                        }
                    )
                }
            }
        }
    }*/

}

/*fun RouteBuilder.newScene(
    route: Router,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    swipeProperties: SwipeProperties? = null,
    content: @Composable (BackStackEntry) -> Unit,
) {
    scene(
        route = route.route,
        deepLinks = deepLinks,
        navTransition = navTransition,
        swipeProperties = swipeProperties,
        content = content
    )
}*/


@Composable
fun CameraScreen() {
    val cameraState = rememberCameraState()
    CameraSection(
        cameraState = cameraState,
        useFrontCamera = false,
        usePinchToZoom = true
    )
    /*val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val result: CameraUiState = uiState) {
        is CameraUiState.Ready -> {
            val cameraState = rememberCameraState()
            //val context = LocalContext.current
            CameraSection(
                cameraState = cameraState,
                useFrontCamera = result.user.useCamFront,
                usePinchToZoom = result.user.usePinchToZoom,
                useTapToFocus = result.user.useTapToFocus,
                lastPicture = result.lastPicture,
                qrCodeText = result.qrCodeText,
                onGalleryClick = onGalleryClick,
                onConfigurationClick = onConfigurationClick,
                onRecording = {
                    //viewModel.toggleRecording(context.contentResolver, cameraState)
                              },
                onTakePicture = { viewModel.takePicture(cameraState) },
                //onAnalyzeImage = viewModel::analyzeImage
            )

            LaunchedEffect(result.throwable) {
                if (result.throwable != null) {
                   // Toast.makeText(context, result.throwable.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        CameraUiState.Initial -> Unit
    }*/
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
