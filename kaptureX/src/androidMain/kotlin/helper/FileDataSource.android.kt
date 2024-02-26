package helper

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import extensions.ImageFile
import java.io.File
import java.util.UUID

actual class FileDataSource(private val context: Context) {
    actual val externalDir: String = "${Environment.DIRECTORY_DCIM}${File.separator}$RELATIVE_PATH"
    actual val currentFileName: String
        get() = "${System.currentTimeMillis()}-${UUID.randomUUID()}"
    private val externalStorage
        get() = Environment.getExternalStoragePublicDirectory(externalDir).apply { mkdirs() }
    private val externalFiles
        get() = externalStorage.listFiles()?.sortedByDescending { it.lastModified() }
    actual val lastPicture: ImageFile
        get() = ImageFile(
            externalFiles?.firstOrNull()?.toUri(),
            context.contentResolver
        )

    fun getFile(
        extension: String = "jpg",
    ): File = File(externalStorage.path, "$currentFileName.$extension").apply {
        if (parentFile?.exists() == false) parentFile?.mkdirs()
        createNewFile()
    }

    val imageContentValues: ContentValues = getContentValues(JPEG_MIME_TYPE)

    val videoContentValues: ContentValues = getContentValues(VIDEO_MIME_TYPE)

    private fun getContentValues(mimeType: String) = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, currentFileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, externalDir)
    }

    companion object {
        private const val JPEG_MIME_TYPE = "image/jpeg"
        private const val VIDEO_MIME_TYPE = "video/mp4"
        const val RELATIVE_PATH: String = "KaptureX"
    }
}