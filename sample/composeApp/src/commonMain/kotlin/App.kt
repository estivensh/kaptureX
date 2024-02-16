import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import extensions.ImageFile
import extensions.toByteArray
import model.CameraOption
import model.Flash
import org.jetbrains.compose.resources.ExperimentalResourceApi
import state.CamSelector
import state.CameraState
import state.ImageCaptureResult
import state.rememberCamSelector
import state.rememberCameraState

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        CameraScreen(
            onGalleryClick = {

            },
            onConfigurationClick = {

            }
        )
    }
}


@Composable
fun CameraScreen(
    //viewModel: CameraViewModel = CameraViewModel(),
    onGalleryClick: () -> Unit,
    onConfigurationClick: () -> Unit,
) {
    val cameraState = rememberCameraState()

    CameraSection(
        cameraState = cameraState,
        useFrontCamera = true,
        usePinchToZoom = false,
        useTapToFocus = false,
        lastPicture = null,
        qrCodeText = "",
        onGalleryClick = onGalleryClick,
        onConfigurationClick = onConfigurationClick,
        onRecording = {
            //viewModel.toggleRecording(context.contentResolver, cameraState)
        },
        onTakePicture = {
            cameraState.takePicture(
                onResult = {
                    when (it) {
                        is ImageCaptureResult.Error -> {
                            println("Estado: ${it.throwable.message}")
                        }

                        is ImageCaptureResult.Success -> println("Estado: ${it.imageFile}")
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


@Composable
fun CameraSection(
    cameraState: CameraState,
    useFrontCamera: Boolean,
    usePinchToZoom: Boolean,
    useTapToFocus: Boolean,
    qrCodeText: String?,
    lastPicture: ImageFile?,
    onTakePicture: () -> Unit,
    onRecording: () -> Unit,
    onGalleryClick: () -> Unit,
    //onAnalyzeImage: (ImageProxy) -> Unit,
    onConfigurationClick: () -> Unit,
) {
    //var flashMode by cameraState.rememberFlashMode()
    var camSelector by rememberCamSelector(if (useFrontCamera) CamSelector.Front else CamSelector.Back)
    var zoomRatio by rememberSaveable { mutableStateOf(cameraState.minZoom) }
    var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
    val hasFlashUnit by rememberUpdatedState(cameraState.hasFlashUnit)
    var cameraOption by rememberSaveable { mutableStateOf(CameraOption.Photo) }
    val isRecording by rememberUpdatedState(cameraState.isRecording)
    var enableTorch = true
    //var enableTorch by cameraState.rememberTorch(initialTorch = false)
    //val imageAnalyzer = cameraState.rememberImageAnalyzer(analyze = onAnalyzeImage)
    CameraPreview(
        cameraState = cameraState,
        camSelector = camSelector,
        //captureMode = cameraOption.toCaptureMode(),
        enableTorch = enableTorch,
        zoomRatio = zoomRatio,
        //imageAnalyzer = ImageAnalyzer(),
        isPinchToZoomEnabled = usePinchToZoom,
        onZoomRatioChanged = {
            zoomHasChanged = true
            zoomRatio = it
        }
    ) {
        BlinkPictureBox(lastPicture, cameraOption == CameraOption.Video)
        CameraInnerContent(
            modifier = Modifier.fillMaxSize(),
            zoomHasChanged = zoomHasChanged,
            zoomRatio = zoomRatio,
            flashMode = Flash.Auto,
            //flashMode = flashMode.toFlash(enableTorch),
            isRecording = isRecording,
            cameraOption = cameraOption,
            hasFlashUnit = hasFlashUnit,
            qrCodeText = qrCodeText,
            isVideoSupported = true,
            //isVideoSupported = cameraState.isVideoSupported,
            onFlashModeChanged = { flash ->
                enableTorch = flash == Flash.Always
                //flashMode = flash.toFlashMode()
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
            onConfigurationClick = onConfigurationClick
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
    qrCodeText: String?,
    lastPicture: ImageFile?,
    isVideoSupported: Boolean,
    onGalleryClick: () -> Unit,
    onFlashModeChanged: (Flash) -> Unit,
    onZoomFinish: () -> Unit,
    onRecording: () -> Unit,
    onTakePicture: () -> Unit,
    onConfigurationClick: () -> Unit,
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
            onConfigurationClick = onConfigurationClick,
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
            qrCodeText = qrCodeText,
            onTakePicture = onTakePicture,
            isRecording = isRecording,
            isVideoSupported = isVideoSupported,
            onRecording = onRecording,
            onSwitchCamera = onSwitchCamera,
            onCameraOptionChanged = onCameraOptionChanged,
        )
    }
}
