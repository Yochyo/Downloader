package de.yochyo.downloader

import de.yochyo.utils.DownloadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.concurrent.LinkedBlockingDeque

internal typealias Download<E> = Triple<String, suspend (e: E) -> Unit, Any> //URL, callback, data (for toResource)

abstract class AbstractDownloader<E> {
    internal val downloads = LinkedBlockingDeque<Download<E>>()

    abstract fun toResource(inputStream: InputStream, data: Any): E

    internal abstract fun startDownloader()

    open fun download(url: String, callback: suspend (e: E) -> Unit, downloadFirst: Boolean = false, data: Any = "") {
        if (downloadFirst) downloads.putFirst(Download(url, callback, data))
        else downloads.putLast(Download(url, callback, data))
    }

    internal suspend fun downloadNextFile() {
        withContext(Dispatchers.IO) {
            val download = downloads.takeLast()
            try {
                val stream = DownloadUtils.getUrlInputStream(download.first)
                if (stream != null) {
                    val resource = toResource(stream, download.third)
                    stream.close()
                    download.second(resource)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun stop() = _stop()
    internal abstract fun _stop()

}