package de.yochyo.downloader

import kotlinx.coroutines.CoroutineScope
import kotlin.math.min

abstract class RegulatingDownloader<E>(var maxCoroutines: Int) : ADownloader<E>(){
    private val lock = Any()
    internal var activeCoroutines = 0
        get() = synchronized(lock) { field }
        set(value) {
            synchronized(lock) { field = value }
        }

    override fun keepCoroutineAliveWhile(scope: CoroutineScope): Boolean = downloads.isNotEmpty()

    override fun onStartCoroutine() {
        ++activeCoroutines
    }

    override fun onStopCoroutine() {
        --activeCoroutines
    }
    override fun onAddDownload() {
        updateJobAmount()
    }

    protected open fun updateJobAmount() {
        while (activeCoroutines < min(maxCoroutines, downloads.size))
            startCoroutine()
    }

    override fun stop() {
        downloads.clear()
    }
}