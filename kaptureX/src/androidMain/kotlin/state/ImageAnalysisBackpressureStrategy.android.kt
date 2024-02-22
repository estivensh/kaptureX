package state

import androidx.camera.core.ImageAnalysis

actual enum class ImageAnalysisBackpressureStrategy(
    internal val strategy: Int
) {
    KeepOnlyLatest(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST),

    /**
     *  add multiple images to the internal image queue and begin dropping frames only when the queue is full, used for blocking operation.
     * */
    BlockProducer(ImageAnalysis.STRATEGY_BLOCK_PRODUCER);

    internal companion object {
        internal fun find(strategy: Int) =
            entries.firstOrNull { it.strategy == strategy } ?: KeepOnlyLatest
    }
}