package de.yochyo.downloader

import de.yochyo.utils.DownloadUtils
import kotlinx.coroutines.*
import java.io.InputStream
import java.util.concurrent.LinkedBlockingDeque

internal typealias Download<E> = Triple<String, suspend (e: E) -> Unit, Any> //URL, callback, data (for toResource)

abstract class ADownloader<E> : IDownloader<E> {
    val config = DownloaderConfig()
    protected val downloads = LinkedBlockingDeque<Download<E>>()

    abstract fun toResource(inputStream: InputStream, context: Any): E

    protected abstract fun keepCoroutineAliveWhile(scope: CoroutineScope): Boolean


    protected open fun onStartCoroutine() {}
    protected open fun onStopCoroutine() {}

    protected open suspend fun onDownloadedResource(e: E) {}
    protected open fun onAddDownload() {}

    override fun download(url: String, callback: suspend (e: E) -> Unit, context: Any) {
        downloads.putLast(Download(url, callback, context))
        onAddDownload()
    }

    override fun downloadNow(url: String, callback: suspend (e: E) -> Unit, context: Any) {
        downloads.putFirst(Download(url, callback, context))
        onAddDownload()
    }

    override suspend fun downloadSync(url: String, context: Any): E? {
        return processNextFile(Download(url, {}, context))
    }

    internal fun startCoroutine() {
        onStartCoroutine()
        GlobalScope.launch(Dispatchers.IO) {
            while (keepCoroutineAliveWhile(this)) {
                try {
                    processNextFile()
                } catch (e: Exception) {
                }
            }
            joinAll()
            onStopCoroutine()
        }
    }

    internal suspend fun processNextFile(download: Download<E> = downloads.takeLast()): E? {
        return withContext(Dispatchers.IO) {
            try {
                val stream = DownloadUtils.getUrlInputStream(download.first)
                if (stream != null) {
                    val result = toResource(stream, download.third)
                    if (config.closeStreamAfterDownload)
                        stream.close()
                    onDownloadedResource(result)
                    launch { download.second(result) }
                    result
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}