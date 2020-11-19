package de.yochyo.downloader


interface IDownloader<E> {
    fun download(url: String, callback: suspend (e: E?) -> Unit, context: Any)
    fun downloadNow(url: String, callback: suspend (e: E?) -> Unit, context: Any)
    suspend fun downloadSync(url: String, context: Any): E?
    fun stop()
}