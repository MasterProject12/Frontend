package com.app.travel.flare.network

import android.util.Log
import com.app.travel.flare.utils.HttpMethods
import com.app.travel.flare.utils.StringUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Networking {

    object API_URLS {
        // base url for the api's
        const val BASE_URL = ""
    }


    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val TAG = Networking::class.java.simpleName

    fun asyncConnection(url: String?, method: String, payload: String, headers: HashMap<String, String>, responseHandler: ResponseHandler) {
        Log.d(TAG, "asyncConnection")
        executor.execute {
            val url1: URL
            var urlConnection: HttpURLConnection? = null
            var `is`: InputStream? = null
            try {
                url1 = URL(url)
                urlConnection = url1.openConnection() as HttpURLConnection
                urlConnection.requestMethod = method
                if (headers != null) {
                    for ((key, value) in headers) {
                        urlConnection!!.setRequestProperty(key, value)
                    }
                }
                if (!StringUtils.isNullOrEmpty(payload) && method.equals(HttpMethods.POST.name, ignoreCase = true)) {
                    urlConnection!!.doOutput = true
                    val bytes = payload.toByteArray(charset("utf-8"))
                    urlConnection.outputStream.write(bytes, 0, bytes.size)
                }
                urlConnection!!.connect()
                val responseCode = urlConnection.responseCode
                if (responseCode == 200) {
                    `is` = urlConnection.inputStream
                    if (`is` != null) {
                        try {
                            val response = readInputStream(`is`)
                            responseHandler.onSuccess(response)
                        } catch (exception: IOException) {
                            responseHandler.onFailure(ResponseHandler.FailureType.RESPONSE, responseCode, null)
                        }
                    } else {
                        responseHandler.onFailure(ResponseHandler.FailureType.RESPONSE, responseCode, null)
                    }
                } else {
                    `is` = urlConnection.errorStream
                    if (`is` != null) {
                        try {
                            val response = readInputStream(`is`)
                            responseHandler.onFailure(ResponseHandler.FailureType.RESPONSE, responseCode, response)
                        } catch (exception: IOException) {
                            responseHandler.onFailure(ResponseHandler.FailureType.RESPONSE, responseCode, null)
                        }
                    } else {
                        responseHandler.onFailure(ResponseHandler.FailureType.RESPONSE, responseCode, null)
                    }
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                responseHandler.onFailure(ResponseHandler.FailureType.EXCEPTION, -1, null)
            } catch (e: IOException) {
                e.printStackTrace()
                responseHandler.onFailure(ResponseHandler.FailureType.EXCEPTION, -1, null)
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                urlConnection?.disconnect()
            }
        }
    }

    @Throws(IOException::class)
    private fun readInputStream(`is`: InputStream): String {
        val sb = StringBuilder()
        val br = BufferedReader(InputStreamReader(`is`))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line)
        }
        return sb.toString()
    }

    interface ResponseHandler {
        fun onSuccess(response: String?)
        fun onFailure(failureType: FailureType?, responseCode: Int, reason: String?)
        enum class FailureType {
            EXCEPTION, RESPONSE
        }
    }

    companion object{
        val INSTANCE: Networking = Networking()
    }
}
