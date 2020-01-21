package de.yochyo.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.min


abstract class RegulatingDownloader<E>(val maxThreads: Int = 1) : AbstractDownloader<E>() {
    private val lock = Any()
    internal var activeCoroutines = 0
        get() = synchronized(lock) { field }
        set(value) {
            synchronized(lock) { field = value }
        }

    internal open fun updateJobAmount() {
        while (activeCoroutines < min(maxThreads, downloads.size))
            startDownloader()
    }

    override fun startDownloader() {
        ++activeCoroutines
        GlobalScope.launch(Dispatchers.IO) {
            while (downloads.isNotEmpty()) {
                try {
                    downloadNextFile()
                } catch (e: Exception) {
                }
            }
            --activeCoroutines
        }
    }

    override fun download(url: String, callback: suspend (e: E) -> Unit, downloadFirst: Boolean, data: Any) {
        super.download(url, callback, downloadFirst, data)
        updateJobAmount()
    }

    override fun _stop() {
        downloads.clear()
    }
}