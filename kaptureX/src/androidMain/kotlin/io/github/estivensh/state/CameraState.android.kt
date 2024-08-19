package io.github.estivensh.state

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.TorchState
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.OutputSize.UNASSIGNED_ASPECT_RATIO
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import io.github.estivensh.extensions.ImageFile
import io.github.estivensh.extensions.compatMainExecutor
import io.github.estivensh.extensions.isImageAnalysisSupported
import io.github.estivensh.helper.FileDataSource

actual class CameraState(private val context: Context) {

    actual val controller: LifecycleCameraController = LifecycleCameraController(context)
    private val mainExecutor = context.compatMainExecutor
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var recordController: Recording? = null
    actual val fileDataSource = FileDataSource(context)

    init {
       // Napier.base(DebugAntilog())
        controller.initializationFuture.addListener(
            {
                resetCamera()
                isInitialized = true
            },
            mainExecutor
        )
    }

    private fun resetCamera() {
        hasFlashUnit = controller.cameraInfo?.hasFlashUnit() ?: false
        startZoom()
    }

    internal actual var flashMode: FlashMode
        get() = FlashMode.find(controller.imageCaptureFlashMode)
        set(value) {
            if (hasFlashUnit && flashMode != value) {
                controller.imageCaptureFlashMode = value.mode
            }
        }

    actual var hasFlashUnit: Boolean
            by mutableStateOf(controller.cameraInfo?.hasFlashUnit() ?: true)
    actual val isZoomSupported: Boolean
            by derivedStateOf { maxZoom != 1F }
    actual var maxZoom: Float
            by mutableFloatStateOf(controller.zoomState.value?.maxZoomRatio ?: INITIAL_ZOOM_VALUE)

    actual var minZoom: Float
            by mutableFloatStateOf(controller.zoomState.value?.minZoomRatio ?: INITIAL_ZOOM_VALUE)

    actual fun startZoom() {
        controller.isPinchToZoomEnabled = false
        val zoom = controller.zoomState.value
        minZoom = zoom?.minZoomRatio ?: INITIAL_ZOOM_VALUE
        maxZoom = zoom?.maxZoomRatio ?: INITIAL_ZOOM_VALUE
    }

    internal actual var scaleType: ScaleType = ScaleType.FillCenter
    internal actual var camSelector: CamSelector = CamSelector.Back
        set(value) {
            when {
                value == field -> {
                    //Napier.e(tag = TAG) { "Device is recording, switch camera is unavailable" }
                }

                !isRecording && hasCamera(value) -> {
                   // Napier.e(tag = TAG) { "Device is recording, switch camera is unavailable" }
                    if (controller.cameraSelector != value.selector) {
                        controller.cameraSelector = value.selector
                        field = value
                        resetCamera()
                    }
                }

                isRecording -> {} //Napier.e(tag = TAG) { "Device is recording, switch camera is unavailable" }
                else -> {} //Napier.e(tag = TAG) { "Device does not have ${value.selector} camera" }
            }
        }
    actual var isRecording: Boolean by mutableStateOf(controller.isRecording)
    actual var isStreaming: Boolean by mutableStateOf(true)
    actual var isInitialized: Boolean by mutableStateOf(false)
    actual fun hasCamera(cameraSelector: CamSelector): Boolean {
        return isInitialized && controller.hasCamera(cameraSelector.selector)
    }

    internal actual var captureMode: CaptureMode = CaptureMode.Image
        set(value) {
            if (field != value) {
                field = value
                updateCaptureMode()
            }
        }

    private var imageAnalyzer: ImageAnalysis.Analyzer? = null
        set(value) {
            field = value
            updateImageAnalyzer(value)
        }

    private fun updateCaptureMode() {
        try {
            var useCases = captureMode.value
            if (captureMode == CaptureMode.Image && isImageAnalysisEnabled) {
                useCases = useCases or IMAGE_ANALYSIS
                updateImageAnalyzer(imageAnalyzer)
            } else {
                updateImageAnalyzer(null)
            }
            controller.setEnabledUseCases(useCases)
        } catch (exception: IllegalStateException) {
           // Napier.e(tag = TAG) { "Use case Image Analysis not supported" }
            controller.setEnabledUseCases(captureMode.value)
        }
    }

    private fun updateImageAnalyzer(
        analyzer: ImageAnalysis.Analyzer? = imageAnalyzer
    ) = with(controller) {
        clearImageAnalysisAnalyzer()
        if (captureMode == CaptureMode.Video) {
            return
        }

        setImageAnalysisAnalyzer(mainExecutor, analyzer ?: return)
    }

    actual var isImageAnalysisSupported: Boolean by mutableStateOf(
        isImageAnalysisSupported(camSelector)
    )

    internal actual var isImageAnalysisEnabled: Boolean = isImageAnalysisSupported
        set(value) {
            if (!isImageAnalysisSupported) {
                //Napier.e(tag = TAG) { "Image analysis is not supported" }
                return
            }
            field = value
            updateCaptureMode()
        }


    @SuppressLint("RestrictedApi")
    actual fun isImageAnalysisSupported(cameraSelector: CamSelector): Boolean {
        return cameraManager?.isImageAnalysisSupported(cameraSelector.selector.lensFacing) ?: false
    }

    internal actual var imageCaptureMode: ImageCaptureMode = ImageCaptureMode.MinLatency
        set(value) {
            if (field != value) {
                field = value
                controller.imageCaptureMode = value.mode
            }
        }
    internal actual var enableTorch: Boolean
        get() = controller.torchState.value == TorchState.ON
        set(value) {
            if (enableTorch != value) {
                controller.enableTorch(hasFlashUnit && value)
            }
        }

    internal actual var imageCaptureTargetSize: ImageTargetSize?
        get() = controller.imageCaptureTargetSize.toImageTargetSize()
        set(value) {
            if (value != imageCaptureTargetSize) {
                controller.imageCaptureTargetSize = value?.toOutputSize()
            }
        }
    actual val initialExposure: Int = INITIAL_EXPOSURE_VALUE
        get() = controller.cameraInfo?.exposureState?.exposureCompensationIndex ?: field

    internal actual fun update(
        camSelector: CamSelector,
        captureMode: CaptureMode,
        scaleType: ScaleType,
        imageCaptureTargetSize: ImageTargetSize?,
        isImageAnalysisEnabled: Boolean,
        isFocusOnTapEnabled: Boolean,
        flashMode: FlashMode,
        zoomRatio: Float,
        imageCaptureMode: ImageCaptureMode,
        enableTorch: Boolean,
        exposureCompensation: Int
    ) {
        this.camSelector = camSelector
        this.captureMode = captureMode
        this.scaleType = scaleType
        this.imageCaptureTargetSize = imageCaptureTargetSize
        this.isImageAnalysisEnabled = isImageAnalysisEnabled
        this.isFocusOnTapEnabled = isFocusOnTapEnabled
        this.flashMode = flashMode
        this.enableTorch = enableTorch
        this.imageCaptureMode = imageCaptureMode
    }

    actual fun takePicture(
        onResult: (ImageCaptureResult) -> Unit
    ) {
        try {
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(
                    context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    fileDataSource.imageContentValues
                )
                .build()
            controller.takePicture(
                outputOptions,
                mainExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        //Napier.i(tag = TAG) { "Capture photo - ${outputFileResults.savedUri}" }
                        onResult(
                            ImageCaptureResult.Success(
                                ImageFile(
                                    uri = outputFileResults.savedUri
                                        ?: fileDataSource.lastPicture.uri,
                                    contentResolver = context.contentResolver
                                )
                            )
                        )
                    }

                    override fun onError(exception: ImageCaptureException) {
                        //Napier.i(tag = TAG) { "Capture photo - ${exception.message}" }
                        onResult(ImageCaptureResult.Error(exception))
                    }

                }
            )
        } catch (exception: Exception) {
            //Napier.i(tag = TAG) { "Capture photo - ${exception.message}" }
            onResult(ImageCaptureResult.Error(exception))
        }
    }

    private fun prepareRecording(
        onError: (VideoCaptureResult.Error) -> Unit,
        onRecordBuild: () -> Recording
    ) {
        try {
            //Napier.i(tag = TAG) { "Prepare recording" }
            isRecording = true
            recordController = onRecordBuild()
        } catch (exception: Exception) {
            //Napier.i(tag = TAG) { "Fail to record! - $exception" }
            isRecording = false
            onError(
                VideoCaptureResult.Error(
                    if (!controller.isVideoCaptureEnabled) {
                        "Video capture is not enabled, please set captureMode as CaptureMode.Video - ${exception.message}"
                    } else "${exception.message}", exception
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    actual fun startRecording(onResult: (VideoCaptureResult) -> Unit) =
        prepareRecording(onResult) {
            //Napier.i(tag = TAG) { "Start recording" }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val mediaStoreOutputOptions = MediaStoreOutputOptions
                        .Builder(
                            context.contentResolver,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        )
                        .setContentValues(fileDataSource.videoContentValues)
                        .build()


                    controller.startRecording(
                        mediaStoreOutputOptions,
                        AudioConfig.create(true),
                        mainExecutor,
                        getConsumerEvent(onResult)
                    )
                }

                else -> {
                    controller.startRecording(
                        FileOutputOptions.Builder(fileDataSource.getFile("mp4")).build(),
                        AudioConfig.create(true),
                        mainExecutor,
                        getConsumerEvent(onResult)
                    )
                }
            }
        }

    private fun getConsumerEvent(
        onResult: (VideoCaptureResult) -> Unit
    ): Consumer<VideoRecordEvent> = Consumer { event ->
        //Napier.i(tag = TAG) { "Video Recorder Event - $event" }
        if (event is VideoRecordEvent.Finalize) {
            isRecording = false
            val result = when {
                !event.hasError() -> VideoCaptureResult.Success(
                    ImageFile(
                        event.outputResults.outputUri,
                        context.contentResolver
                    )
                )

                else -> VideoCaptureResult.Error(
                    "Video error code: ${event.error}",
                    event.cause
                )
            }
            recordController = null
            onResult(result)
        }
    }

    actual fun stopRecording() {
        recordController?.stop()?.also {
            isRecording = false
        }
    }

    actual fun pauseRecording() {
        recordController?.pause()
    }

    actual fun resumeRecording() {
        recordController?.resume()
    }

    actual fun toggleRecording(onResult: (VideoCaptureResult) -> Unit) {
        when (isRecording) {
            true -> stopRecording()
            false -> startRecording(onResult)
        }
    }

    internal actual var isFocusOnTapEnabled: Boolean
        get() = controller.isTapToFocusEnabled
        set(value) {
            controller.isTapToFocusEnabled = value
        }

    actual companion object {
        private val TAG = this::class.java.name
        actual val INITIAL_ZOOM_VALUE: Float = 1F
        actual val INITIAL_EXPOSURE_VALUE: Int = 0
    }
}

private fun CameraController.OutputSize?.toImageTargetSize(): ImageTargetSize? {
    return this?.let {
        if (it.aspectRatio != UNASSIGNED_ASPECT_RATIO) {
            ImageTargetSize(aspectRatio = it.aspectRatio)
        } else {
            ImageTargetSize(size = it.resolution)
        }
    }
}

@Composable
actual fun rememberCameraState(): CameraState {
    val context = LocalContext.current
    return remember { CameraState(context) }
}

@Composable
actual fun rememberCamSelector(selector: CamSelector): MutableState<CamSelector> =
    rememberSaveable(saver = CamSelector.Saver) {
        mutableStateOf(selector)
    }

@Composable
actual fun CameraState.rememberFlashMode(
    initialFlashMode: FlashMode,
    useSaver: Boolean
): MutableState<FlashMode> = rememberConditionalState(
    initialValue = initialFlashMode,
    defaultValue = FlashMode.Off,
    useSaver = useSaver,
    predicate = hasFlashUnit
)

@Composable
actual fun CameraState.rememberTorch(
    initialTorch: Boolean,
    useSaver: Boolean
): MutableState<Boolean> = rememberConditionalState(
    initialValue = initialTorch,
    defaultValue = false,
    useSaver = useSaver,
    predicate = hasFlashUnit
)