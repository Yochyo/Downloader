package de.yochyo.utils

import de.yochyo.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object DownloadUtils {
    suspend fun getUrlSource(urlToRead: String): List<String> {
        return withContext(Dispatchers.IO) {
            val list = LinkedList<String>()
            try {
                val stream = getUrlInputStream(urlToRead)
                if (stream != null) {
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { bufferedReader ->
                        var inputLine: String? = bufferedReader.readLine()
                        while (inputLine != null) {
                            list += inputLine
                            inputLine = bufferedReader.readLine()
                        }
                        stream.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            list
        }
    }

    suspend fun getJson(urlToRead: String): JSONArray? {
        var array: JSONArray? = null
        try {
            val
                    array = JSONArray(String(getUrlInputStream(urlToRead)!!.readBytes()))
        } catch (e: Exception) {
        }
        return array
    }

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