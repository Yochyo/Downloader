package de.yochyo.downloader

import de.yochyo.utils.DownloadUtils
import kotlinx.coroutines.*
import java.io.InputStream
import java.util.*

internal typealias Download<E> = Triple<String, suspend (e: E) -> Unit, Any> //URL, callback, data (for toResource)

abstract class AbstractDownloader<E> {
    private val lock = Any()
    internal val downloads = object : Stack<Download<E>>() {
        override fun pop() = synchronized(lock) { super.pop() }
        override fun push(item: Download<E>?) = synchronized(lock) { super.push(item) }
    }

    abstract fun toResource(inputStream: InputStream, data: Any): E

    internal abstract fun startDownloader()

    open fun download(url: String, callback: suspend (e: E) -> Unit, data: Any = "") {
        downloads += Download(url, callback, data)
    }

    internal suspend fun downloadNextFile() {
        withContext(Dispatchers.IO) {
            val download = downloads.pop()
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