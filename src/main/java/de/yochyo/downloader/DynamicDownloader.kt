package de.yochyo.downloader

abstract class DynamicDownloader<E>(var proportion: Int = 2) : RegulatingDownloader<E>(Integer.MAX_VALUE) {
    override fun updateJobAmount() {
        while (downloads.size > activeCoroutines * proportion)
            startCoroutine()
    }
}