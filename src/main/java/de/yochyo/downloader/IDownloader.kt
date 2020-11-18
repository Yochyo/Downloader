package de.yochyo.downloader


typealias DownloadCallback<E> = suspend (e: E?, url: String, context: Any) -> Unit

interface IDownloader<E> {
    fun download(url: String, callback: DownloadCallback<E>, context: Any)
    fun downloadNow(url: String, callback: DownloadCallback<E>, context: Any)
    suspend fun downloadSync(url: String, context: Any): E?
    fun stop()
}