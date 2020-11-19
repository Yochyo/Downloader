package de.yochyo.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object DownloadUtils {
    suspend fun getUrlInputStream(url: String): InputStream? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.addRequestProperty("User-Agent", "Mozilla/5.00")
                conn.requestMethod = "GET"
                val input = conn.inputStream
                println(input.markSupported())
                input
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}