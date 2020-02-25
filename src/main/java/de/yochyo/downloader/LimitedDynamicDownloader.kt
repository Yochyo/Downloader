package de.yochyo.downloader

import kotlin.math.min

abstract class LimitedDynamicDownloader<E>(proportion: Int = 2, maxCoroutines: Int) : DynamicDownloader<E>(proportion) {
    init {
        this.maxCoroutines = maxCoroutines
    }

    override fun updateJobAmount() {
        while (downloads.size > activeCoroutines * proportion && activeCoroutines < min(maxCoroutines, downloads.size))
            startCoroutine()
    }
}