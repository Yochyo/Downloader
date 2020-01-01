package de.yochyo.downloader

import kotlinx.coroutines.*

@Deprecated("Use RegulatingDownloader instead")
abstract class Downloader<E>(coroutines: Int) : AbstractDownloader<E>() {
    private var enabled = true

    init {
        for (i in 0 until coroutines) {
            startDownloader()
        }
    }

    override fun startDownloader() {
        GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    downloadNextFile()
                } catch (e: Exception) {
                    delay(50)
                }
            }
        }
    }

    override fun _stop() {
        enabled = false
    }
}