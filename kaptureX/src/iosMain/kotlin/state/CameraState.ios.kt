package state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import extensions.ImageFile
import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureDevicePositionUnspecified
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureFileOutput
import platform.AVFoundation.AVCaptureFileOutputRecordingDelegateProtocol
import platform.AVFoundation.AVCaptureInput
import platform.AVFoundation.AVCaptureMovieFileOutput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVCaptureVideoStabilizationModeAuto
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.exposureTargetOffset
import platform.AVFoundation.fileDataRepresentation
import platform.AVFoundation.flashMode
import platform.AVFoundation.focusPointOfInterestSupported
import platform.AVFoundation.hasFlash
import platform.AVFoundation.isTorchActive
import platform.AVFoundation.position
import platform.AVFoundation.setAutomaticallyAdjustsFaceDrivenAutoFocusEnabled
import platform.AVFoundation.setFlashMode
import platform.AVFoundation.videoMinZoomFactorForCenterStage
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.fileURLWithPathComponents
import platform.Foundation.pathComponents
import platform.UIKit.UIScreen
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class CameraState {

    var captureSession: AVCaptureSession
    actual val controller: AVCaptureDevice =
        AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: AVCaptureDevice()
    val photoOutput = AVCapturePhotoOutput()
    private lateinit var movieFileOutput: AVCaptureMovieFileOutput
    private lateinit var outputFileURL: NSURL

    init {
        captureSession = AVCaptureSession()
    }

    @Suppress("UNCHECKED_CAST")
    fun initCamera(
        currentCamSelector: CamSelector,
        currentFlashMode: FlashMode,
        videoPreviewLayer: AVCaptureVideoPreviewLayer,
        onInit: (AVCaptureSession) -> Unit
    ) {
        val avCaptureDevices =
            AVCaptureDevice.devicesWithMediaType(AVMediaTypeVideo) as List<AVCaptureDevice>

        val captureDevice = when (currentCamSelector) {
            CamSelector.Front -> avCaptureDevices
                .firstOrNull { it.position == AVCaptureDevicePositionFront }

            CamSelector.Back -> avCaptureDevices
                .firstOrNull { it.position == AVCaptureDevicePositionBack }
        }

        val inputs = captureSession.inputs as List<AVCaptureInput>
        inputs.forEach { captureSession.removeInput(it) }

        val input = captureDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }
        if (input != null && captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        // Configurar el flash
        captureDevice?.lockForConfiguration(null)
        captureDevice?.setFlashMode(currentFlashMode.mode)
        captureDevice?.unlockForConfiguration()

        // Configurar la captura de fotos
        captureSession.addOutput(photoOutput)

        // Configurar la capa de vista previa del video
        videoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        videoPreviewLayer.frame = UIScreen.mainScreen.bounds

        // Iniciar la sesión de captura
        onInit(captureSession)
    }

    internal actual var flashMode: FlashMode
        get() = FlashMode.find(controller.flashMode)
        set(value) {
            if (hasFlashUnit && flashMode != value) {
                controller.setFlashMode(value.mode)
            }
        }

    actual var hasFlashUnit: Boolean by mutableStateOf(controller.hasFlash)
    actual val isZoomSupported: Boolean
            by derivedStateOf { maxZoom != 1F }
    actual var maxZoom: Float
            by mutableFloatStateOf(
                controller.activeFormat.videoMaxZoomFactor.toFloat()
            )

    actual companion object {
        private val TAG = this::class.simpleName
        actual val INITIAL_ZOOM_VALUE: Float = 1F
        actual val INITIAL_EXPOSURE_VALUE: Int = 0
    }

    actual var minZoom: Float
            by mutableFloatStateOf(
                controller.activeFormat.videoMinZoomFactorForCenterStage.toFloat()
            )

    actual fun startZoom() {
        val zoom = controller.activeFormat
        minZoom = zoom.videoMinZoomFactorForCenterStage.toFloat() ?: INITIAL_ZOOM_VALUE
        maxZoom = zoom.videoMaxZoomFactor.toFloat() ?: INITIAL_ZOOM_VALUE
    }

    internal actual var scaleType: ScaleType = ScaleType.Fill
    internal actual var camSelector: CamSelector = CamSelector.Back
        set(value) {
            when {
                value == field -> Unit
                !isRecording && hasCamera(value) -> {
                    if (controller.position != value.selector) {
                        field = value
                    }
                }

                isRecording -> Napier.e(tag = TAG) { "Device is recording, switch camera is unavailable" }
                else -> Napier.e(tag = TAG) { "Device does not have ${value.selector} camera" }
            }
        }
    actual var isRecording: Boolean by mutableStateOf(false)
    internal actual var captureMode: CaptureMode = CaptureMode.Image
        set(value) {
            if (field != value) {
                field = value
            }
        }
    internal actual var imageCaptureMode: ImageCaptureMode
        get() = ImageCaptureMode.V
        set(value) {}
    actual var isImageAnalysisSupported: Boolean by mutableStateOf(
        isImageAnalysisSupported(camSelector)
    )
    internal actual var isImageAnalysisEnabled: Boolean = isImageAnalysisSupported
        set(value) {
            if (!isImageAnalysisSupported) {
                println("Image analysis is not supported")
                return
            }
            field = value
        }

    actual var isStreaming: Boolean by mutableStateOf(false)
    actual var isInitialized: Boolean by mutableStateOf(false)

    actual fun hasCamera(cameraSelector: CamSelector): Boolean {
        val devices = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            mutableListOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            AVMediaTypeVideo,
            AVCaptureDevicePositionUnspecified
        )
        return devices.devices.isNotEmpty()
    }

    actual fun isImageAnalysisSupported(cameraSelector: CamSelector): Boolean {
        return true
    }

    internal actual var enableTorch: Boolean
        get() = controller.hasFlash && controller.isTorchActive()
        set(value) {

        }
    internal actual var imageCaptureTargetSize: ImageTargetSize?
        get() = null
        set(value) {

        }
    actual val initialExposure: Int
        get() = controller.exposureTargetOffset.toInt()

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
        //setExposureCompensation(exposureCompensation)
        //setZoomRatio(zoomRatio)
    }

    actual fun takePicture(
        onResult: (ImageCaptureResult) -> Unit
    ) {
        val photoSettings = AVCapturePhotoSettings.photoSettings()
        photoOutput.capturePhotoWithSettings(
            photoSettings,
            object : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
                override fun captureOutput(
                    output: AVCapturePhotoOutput,
                    didFinishProcessingPhoto: AVCapturePhoto,
                    error: NSError?
                ) {
                    if (error == null) {
                        didFinishProcessingPhoto.fileDataRepresentation()?.let { photoData ->
                            onResult(ImageCaptureResult.Success(ImageFile(photoData)))
                        }
                    } else {
                        onResult(ImageCaptureResult.Error(Exception(error.localizedDescription())))
                    }
                }
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(BetaInteropApi::class)
    actual fun startRecording(onResult: (VideoCaptureResult) -> Unit) {
        autoreleasepool {
            captureSession = AVCaptureSession()

            val camera =
                AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: AVCaptureDevice()
            setupVideoRecording(camera)

            captureSession.startRunning()

            // Configurar el archivo de salida para guardar el video

            val documentsPath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).firstOrNull()
            outputFileURL = NSURL.fileURLWithPath("$documentsPath/output.mov")
            val paths = NSFileManager.defaultManager()
                .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask) as List<NSURL>
            val pathComponents = paths[0].pathComponents ?: emptyList<String>()
            val newPathComponents = pathComponents + "output.mov"
            val fileURL = NSURL.fileURLWithPathComponents(newPathComponents as List<*>)

            NSFileManager.defaultManager.removeItemAtURL(fileURL ?: NSURL(), error = null)

            // Iniciar la grabación de video
            movieFileOutput.startRecordingToOutputFileURL(
                fileURL ?: NSURL(),
                recordingDelegate = object : NSObject(),
                    AVCaptureFileOutputRecordingDelegateProtocol {
                    override fun captureOutput(
                        output: AVCaptureFileOutput,
                        didFinishRecordingToOutputFileAtURL: NSURL,
                        fromConnections: List<*>,
                        error: NSError?
                    ) {
                        if (error == null) {
                            println("Estado Success: ${didFinishRecordingToOutputFileAtURL.absoluteString}")
                            onResult(
                                VideoCaptureResult.Success(
                                    ImageFile(
                                        didFinishRecordingToOutputFileAtURL.absoluteString.orEmpty()
                                    )
                                )
                            )
                        } else {
                            println("Estado Error: ${error.localizedDescription()} ${error.code} ${error.domain} ")
                            onResult(
                                VideoCaptureResult.Error(
                                    message = error.localizedDescription,
                                    throwable = null
                                )
                            )
                        }
                    }
                })
            isRecording = true
        }
    }

    actual fun stopRecording() {
        movieFileOutput.stopRecording().also {
            isRecording = false
        }
    }

    actual fun pauseRecording() {
        movieFileOutput.stopRecording()
    }

    actual fun resumeRecording() {}


    actual fun toggleRecording(onResult: (VideoCaptureResult) -> Unit) {
        when (isRecording) {
            true -> stopRecording()
            false -> startRecording(onResult)
        }
    }

    private fun setupVideoRecording(camera: AVCaptureDevice) {
        captureSession.beginConfiguration()
        val videoInput = AVCaptureDeviceInput.deviceInputWithDevice(camera, null) as? AVCaptureInput
        if (videoInput != null && captureSession.canAddInput(videoInput)) {
            captureSession.addInput(videoInput)
        }

        val audioDevice =
            AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeAudio) ?: AVCaptureDevice()
        val audioInput =
            AVCaptureDeviceInput.deviceInputWithDevice(audioDevice, null) ?: AVCaptureDeviceInput()
        if (captureSession.canAddInput(audioInput)) {
            captureSession.addInput(audioInput)
        }

        movieFileOutput = AVCaptureMovieFileOutput()
        if (captureSession.canAddOutput(movieFileOutput)) {
            captureSession.addOutput(movieFileOutput)
        }

        val connection = movieFileOutput.connectionWithMediaType(AVMediaTypeVideo)
        if (connection?.isVideoStabilizationSupported() == true) {
            connection.preferredVideoStabilizationMode = AVCaptureVideoStabilizationModeAuto
        }

        captureSession.commitConfiguration()
    }

    internal actual var isFocusOnTapEnabled: Boolean
        get() = controller.focusPointOfInterestSupported
        set(value) {
            controller.setAutomaticallyAdjustsFaceDrivenAutoFocusEnabled(value)
        }


}

@Composable
actual fun rememberCameraState(): CameraState {
    return remember { CameraState() }
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