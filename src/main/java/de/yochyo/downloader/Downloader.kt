package de.yochyo.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class Downloader<E>(coroutines: Int) : AbstractDownloader<E>() {
    private var enabled = true

    init {
        for (i in 0 until coroutines) {
            startDownloader()
        }
    }

    override fun startDownloader() {
        GlobalScope.launch(Dispatchers.IO) {
            while (enabled) {
                try {
                    downloadFile(downloads.pop())
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun _stop() {
        enabled = false
    }
}