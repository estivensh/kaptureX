import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import extensions.ImageFile
import io.github.aakira.napier.Napier
import model.CameraOption
import model.Flash
import model.toCaptureMode
import model.toFlash
import model.toFlashMode
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.SwipeProperties
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import state.CamSelector
import state.CameraState
import state.FlashMode
import state.ImageCaptureResult
import state.VideoCaptureResult
import state.rememberCamSelector
import state.rememberCameraState
import state.rememberFlashMode

@Composable
fun App() {
    PreComposeApp {
        MaterialTheme {
            val navigator = rememberNavigator()
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = Router.Camera.route
            ) {
                newScene(Router.Camera) {
                    CameraScreen(
                        onGalleryClick = {
                            navigator.navigater(Router.Gallery)
                        }
                    )
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
    }

}

fun RouteBuilder.newScene(
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
}


@Composable
fun CameraScreen(
    onGalleryClick: () -> Unit,
) {
    val cameraState = rememberCameraState()
    var lastPicture by remember { mutableStateOf<ImageFile?>(null) }

    CameraSection(
        cameraState = cameraState,
        useFrontCamera = false,
        usePinchToZoom = true,
        useTapToFocus = true,
        lastPicture = lastPicture,
        onGalleryClick = onGalleryClick,
        onRecording = {
            cameraState.toggleRecording {
                when (it) {
                    is VideoCaptureResult.Error -> {

                    }
                    is VideoCaptureResult.Success -> {
                        lastPicture = it.imageFile
                    }
                }
            }
            //viewModel.toggleRecording(context.contentResolver, cameraState)
        },
        onTakePicture = {
            cameraState.takePicture(
                onResult = {
                    when (it) {
                        is ImageCaptureResult.Error -> {
                            println("Estado: ${it.throwable.message}")
                            Napier.d { "${it.throwable.message}" }
                        }

                        is ImageCaptureResult.Success -> {
                            lastPicture = it.imageFile
                            println("Estado: ${it.imageFile}")
                            Napier.d { "${it.imageFile}" }
                        }
                    }
                }
            )
            //viewModel.takePicture(cameraState)
        },
        //onAnalyzeImage = viewModel::analyzeImage
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
    usePinchToZoom: Boolean,
    useTapToFocus: Boolean,
    lastPicture: ImageFile?,
    onTakePicture: () -> Unit,
    onRecording: () -> Unit,
    onGalleryClick: () -> Unit,
    //onAnalyzeImage: (ImageProxy) -> Unit,
) {
    var flashMode by cameraState.rememberFlashMode(FlashMode.valueOf("On"))
    var camSelector by rememberCamSelector(if (useFrontCamera) CamSelector.Front else CamSelector.Back)
    var zoomRatio by rememberSaveable { mutableStateOf(cameraState.minZoom) }
    var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
    val hasFlashUnit by rememberUpdatedState(cameraState.hasFlashUnit)
    var cameraOption by rememberSaveable { mutableStateOf(CameraOption.Video) }
    val isRecording by rememberUpdatedState(cameraState.isRecording)
    var enableTorch = true
    //var enableTorch by cameraState.rememberTorch(initialTorch = false)
    //val imageAnalyzer = cameraState.rememberImageAnalyzer(analyze = onAnalyzeImage)
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
        onSwitchToFront = { bitmap ->
            /*Cloudy(radius = 20) { bitmap.toImageBitmap()
                ?.let { Image(it, contentDescription = null) } }*/
        },
        onSwitchToBack = { bitmap ->
            /*Cloudy(radius = 20) { bitmap.toImageBitmap()
                ?.let { Image(it, contentDescription = null) } }*/
        }
    ) {
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
                flashMode = flash.toFlashMode()
            },
            onZoomFinish = { zoomHasChanged = false },
            lastPicture = lastPicture,
            onTakePicture = onTakePicture,
            onRecording = onRecording,
            onSwitchCamera = {
                /*if (cameraState.isStreaming) {
                    camSelector = camSelector.inverse
                }*/
                camSelector = camSelector.inverse
            },
            onCameraOptionChanged = { cameraOption = it },
            onGalleryClick = onGalleryClick,
        )
    }
}

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
