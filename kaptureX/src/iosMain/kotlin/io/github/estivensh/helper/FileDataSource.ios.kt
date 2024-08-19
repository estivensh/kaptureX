package io.github.estivensh.helper

import io.github.estivensh.extensions.ImageFile
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970

actual class FileDataSource {
    actual val externalDir: String = NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory, NSUserDomainMask, true
    ).firstOrNull().toString()
    actual val currentFileName: String
        get() = "${NSDate().timeIntervalSince1970}-${NSUUID().UUIDString}"
    private val externalStorage
        get() = NSFileManager.defaultManager()

    @Suppress("UNCHECKED_CAST")
    private val externalFiles
        get() = externalStorage.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ) as List<NSURL>
    actual val lastPicture: ImageFile
        get() = ImageFile(externalFiles[0].dataRepresentation)
}