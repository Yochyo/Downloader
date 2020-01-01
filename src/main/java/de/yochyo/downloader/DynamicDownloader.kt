package de.yochyo.downloader

abstract class DynamicDownloader<E>(val proportion: Int = 2) : RegulatingDownloader<E>() {
    override fun updateJobAmount() {
        while (downloads.size > activeCoroutines * proportion)
            startDownloader()
    }
}