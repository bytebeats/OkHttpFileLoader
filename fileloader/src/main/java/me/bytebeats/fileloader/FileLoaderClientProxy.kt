package me.bytebeats.fileloader

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

/**
 * Created by bytebeats on 2021/8/10 : 15:52
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileLoaderClientProxy(private val client: OkHttpClient? = null) {

    private var mClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .build()

    init {
        if (client != null) {
            mClient = client
        }
    }


    private fun newCall(request: Request): Call = mClient.newCall(request)

    fun cancelRequest(tag: String?): Boolean {
        if (!tag.isNullOrEmpty()) {
            for (runningCall in mClient.dispatcher.runningCalls()) {
                if (tag == runningCall.request().tag()) {
                    runningCall.cancel()
                    return true
                }
            }
            for (queuedCall in mClient.dispatcher.queuedCalls()) {
                if (tag == queuedCall.request().tag()) {
                    queuedCall.cancel()
                    return true
                }
            }
        }
        return false
    }

    @Throws(IOException::class)
    private fun execute(request: Request): Response {
        return newCall(request).execute()
    }

    private fun executeAsync(request: Request, callback: Callback) {
        newCall(request).enqueue(callback)
    }

    private fun buildRequest(url: String, headers: Map<String, String>?, tag: String?): Request {
        val builder = Request.Builder().url(url).tag(tag)
        headers?.let {
            it.entries.forEach { entry ->
                builder.header(entry.key, entry.value)
            }
        }
        return builder.build()
    }

    fun doGet(url: String, headers: Map<String, String>?, tag: String?): String? {
        val resp = execute(buildRequest(url, headers, tag))
        return if (resp.isSuccessful) {
            resp.body?.string()
        } else {
            resp.message
        }
    }

    fun doGetAsync(url: String, headers: Map<String, String>?, tag: String?, callback: Callback) {
        executeAsync(buildRequest(url, headers, tag), callback)
    }

    @Throws(IOException::class)
    fun doGetStream(url: String, headers: Map<String, String>?, tag: String?): InputStream? {
        val resp = execute(buildRequest(url, headers, tag))
        if (resp.isSuccessful) {
            return resp.body?.byteStream()
        } else {
            throw IOException("Unexpected code: $resp")
        }
    }

    private fun buildFormRequest(
        url: String,
        headers: Map<String, String>?,
        params: Map<String, String?>?,
        tag: String?
    ): Request {
        val requestBuilder = Request.Builder().url(url).tag(tag)
        headers?.let {
            it.entries.forEach { entry -> requestBuilder.header(entry.key, entry.value) }
        }
        val formBuilder = FormBody.Builder()
        params?.let {
            for (entry in it.entries) {
                formBuilder.add(entry.key, entry.value ?: "")
            }
        }
        return requestBuilder.post(formBuilder.build()).build()
    }

    fun doPost(
        url: String,
        headers: Map<String, String>?,
        params: Map<String, String?>?,
        tag: String?
    ): String? {
        val resp = execute(buildFormRequest(url, headers, params, tag))
        return if (resp.isSuccessful) {
            resp.body?.string()
        } else {
            resp.message
        }
    }

    fun doPostAsync(
        url: String,
        headers: Map<String, String>?,
        params: Map<String, String?>?,
        tag: String?,
        callback: Callback
    ) {
        executeAsync(buildFormRequest(url, headers, params, tag), callback)
    }

    private fun buildJsonRequest(
        url: String,
        headers: Map<String, String>?,
        postJsonBody: String,
        tag: String?
    ): Request {
        val requestBuilder = Request.Builder().url(url).tag(tag)
            .post(postJsonBody.toRequestBody("application/json".toMediaTypeOrNull()))
        headers?.let {
            for (entry in it.entries) {
                requestBuilder.header(entry.key, entry.value)
            }
        }
        return requestBuilder.build()
    }

    fun doPostJson(
        url: String,
        headers: Map<String, String>?,
        postJsonBody: String,
        tag: String?
    ): String? {
        val resp = execute(buildJsonRequest(url, headers, postJsonBody, tag))
        return if (resp.isSuccessful) {
            resp.body?.string()
        } else {
            resp.message
        }
    }

    fun doPostJsonAsync(
        url: String,
        headers: Map<String, String>?,
        postJsonBody: String,
        tag: String?,
        callback: Callback
    ) {
        executeAsync(buildJsonRequest(url, headers, postJsonBody, tag), callback)
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 30L
    }
}