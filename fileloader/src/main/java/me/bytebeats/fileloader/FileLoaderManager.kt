package me.bytebeats.fileloader

import okhttp3.Callback
import java.io.IOException
import java.io.InputStream

/**
 * Created by bytebeats on 2021/8/10 : 16:57
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileLoaderManager {

    val mClientProxy by lazy { FileLoaderClientProxy() }

    companion object {
        private var manager: FileLoaderManager? = null

        private fun getInstance(): FileLoaderManager {
            if (manager == null) {
                manager = FileLoaderManager()
            }
            return manager!!
        }

        fun get(url: String, headers: Map<String, String>?, tag: String?): String? {
            return getInstance().mClientProxy.doGet(url, headers, tag)
        }

        fun get(url: String): String? {
            return get(url, null, null)
        }

        fun getInputStream(url: String, headers: Map<String, String>?, tag: String?): InputStream? {
            return try {
                getInstance().mClientProxy.doGetStream(url, headers, tag)
            } catch (ignore: IOException) {
                null
            }
        }

        fun getInputStream(url: String): InputStream? {
            return getInputStream(url, null, null)
        }

        fun postForm(
            url: String,
            headers: Map<String, String>?,
            params: Map<String, String?>?,
            tag: String?
        ): String? {
            return getInstance().mClientProxy.doPost(url, headers, params, tag)
        }

        fun postForm(url: String, params: Map<String, String?>?): String? {
            return postForm(url, null, params, null)
        }

        fun postJson(
            url: String,
            headers: Map<String, String>?,
            postJsonBody: String,
            tag: String?
        ): String? {
            return try {
                getInstance().mClientProxy.doPostJson(url, headers, postJsonBody, tag)
            } catch (ignore: IOException) {
                null
            }
        }

        fun postJson(url: String, postJsonBody: String): String? {
            return postJson(url, null, postJsonBody, null)
        }

        fun cancelRequest(tag: String?): Boolean {
            return getInstance().mClientProxy.cancelRequest(tag)
        }

        fun getAsync(url: String, callback: Callback) {
            getInstance().mClientProxy.doGetAsync(url, null, null, callback)
        }

        fun postFormAsync(url: String, params: Map<String, String?>?, callback: Callback) {
            getInstance().mClientProxy.doPostAsync(url, null, params, null, callback)
        }

        fun postJsonAsync(url: String, postJsonBody: String, callback: Callback) {
            getInstance().mClientProxy.doPostJsonAsync(url, null, postJsonBody, null, callback)
        }
    }
}