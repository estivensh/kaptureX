package state

import androidx.camera.core.ImageAnalysis

actual class ImageAnalyzer(
    private val cameraState: CameraState,
    imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy,
    imageAnalysisTargetSize: ImageTargetSize?,
    imageAnalysisImageQueueDepth: Int,
    internal var analyzer: ImageAnalysis.Analyzer,
) {

    init {
        updateCameraState(
            imageAnalysisBackpressureStrategy,
            imageAnalysisTargetSize,
            imageAnalysisImageQueueDepth
        )
    }

    private fun updateCameraState(
        imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy,
        imageAnalysisTargetSize: ImageTargetSize?,
        imageAnalysisImageQueueDepth: Int,
    ) = with(cameraState) {
        /*this.imageAnalysisBackpressureStrategy = imageAnalysisBackpressureStrategy.strategy
        this.imageAnalysisTargetSize = imageAnalysisTargetSize?.toOutputSize()
        this.imageAnalysisImageQueueDepth = imageAnalysisImageQueueDepth*/
    }

    /**
     * Update actual image analysis instance.
     * */
    /*public fun update(
        imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy = ImageAnalysisBackpressureStrategy.find(
            cameraState.imageAnalysisBackpressureStrategy
        ),
        imageAnalysisTargetSize: ImageTargetSize? = ImageTargetSize(cameraState.imageAnalysisTargetSize),
        imageAnalysisImageQueueDepth: Int = cameraState.imageAnalysisImageQueueDepth,
        analyzer: ImageAnalysis.Analyzer = this.analyzer,
    ) {
        updateCameraState(
            imageAnalysisBackpressureStrategy,
            imageAnalysisTargetSize,
            imageAnalysisImageQueueDepth
        )
        this.analyzer = analyzer
    }*/
}