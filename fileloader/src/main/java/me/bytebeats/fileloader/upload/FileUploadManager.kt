package me.bytebeats.fileloader.upload

import android.os.Handler
import android.os.Looper

/**
 * Created by bytebeats on 2021/8/12 : 10:36
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileUploadManager private constructor() {
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var mFileUploadOptions: FileUploadOptions<String>? = null

    @Synchronized
    fun init(fileUploadOptions: FileUploadOptions<String>?) {
        if (fileUploadOptions == null) {
            throw IllegalArgumentException("FileUploadOptions can not be null")
        }
        mFileUploadOptions = fileUploadOptions
    }

    private fun ensureValidFileUploadOptions() {
        if (mFileUploadOptions == null) {
            throw IllegalStateException("Please call init() before use.")
        }
    }

    companion object {
        @Volatile
        private var instance: FileUploadManager? = null
        fun instance(): FileUploadManager {
            if (instance == null) {
                synchronized(FileUploadManager::class) {
                    if (instance == null) {
                        instance = FileUploadManager()
                    }
                }
            }
            return instance!!
        }
    }
}