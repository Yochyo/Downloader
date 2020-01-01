package de.yochyo.downloader

import kotlinx.coroutines.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

internal typealias Download<E> = Pair<String, suspend (e: E) -> Unit>

abstract class AbstractDownloader<E> {
    private val lock = Any()
    internal val downloads = object : Stack<Download<E>>() {
        override fun pop() = synchronized(lock) { super.pop() }
        override fun push(item: Download<E>?) = synchronized(lock) { super.push(item) }
    }

    abstract fun toResource(inputStream: InputStream): E

    open fun download(url: String, callback: suspend (e: E) -> Unit) {
        downloads += Download(url, callback)
    }

    internal open fun startDownloader() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    downloadFile(downloads.pop())
                } catch (e: Exception) {
                    delay(50)
                }
            }
        }
    }

    internal open suspend fun downloadFile(download: Download<E>) {
        /*
        withContext(Dispatchers.IO) {
            try {
                val stream = getUrlInputStream(download.first)
                if (stream != null) {
                    val resource = toResource(stream)
                    stream.close()
                    download.second(resource)
                }
            } finally {
            }
        }
         */
    }

    fun stop() = _stop()
    internal abstract fun _stop()

    internal suspend fun getUrlInputStream(url: String): InputStream? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.addRequestProperty("User-Agent", "Mozilla/5.00")
                conn.requestMethod = "GET"
                conn.inputStream
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}