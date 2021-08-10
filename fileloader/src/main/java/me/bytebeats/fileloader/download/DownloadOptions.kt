package me.bytebeats.fileloader.download

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import me.bytebeats.fileloader.DefaultOptionsFactory
import java.io.File
import java.util.concurrent.Executor

/**
 * Created by bytebeats on 2021/8/10 : 19:26
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class DownloadOptions private constructor(builder: Builder) {
    private val mContext = builder.context
    val mTaskExecutor = builder.taskExecutor
    private val mUsingInjectedExecutor = builder.usingInjectedExecutor
    val mCacheDir = builder.cacheDir

    class Builder(
        val context: Context,
        var taskExecutor: Executor? = null,
        var cacheDir: File? = null,
        private var threadPoolSize: Int = DEFAULT_THREAD_POOS_SIZE,
    ) {
        var threadPriority: Int = DEFAULT_THREAD_PRIORITY
            set(value) {
                field = if (value > Thread.MAX_PRIORITY) {
                    Thread.MAX_PRIORITY
                } else if (value < Thread.MIN_PRIORITY) {
                    Thread.MIN_PRIORITY
                } else {
                    value
                }
            }
        var usingInjectedExecutor: Boolean = false
            private set

        fun build(): DownloadOptions {
            ensureParametersValid()
            return DownloadOptions(this)
        }

        private fun ensureParametersValid() {
            if (taskExecutor == null) {
                taskExecutor = DefaultOptionsFactory.createExecutor(threadPoolSize, threadPriority)
            } else {
                usingInjectedExecutor = true
            }
            if (cacheDir == null) {
                cacheDir = getOwnCacheDirectory(context, "Downloads")
            }
        }
    }

    companion object {
        private const val TAG = "DownloadOptions"
        const val DEFAULT_THREAD_POOS_SIZE = 3
        const val DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1

        fun getOwnCacheDirectory(context: Context, cacheDir: String): File? {
            var appCacheDir: File? = null
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                && hasExternalStoragePermission(context)
            ) {
                appCacheDir = File(Environment.getExternalStorageDirectory(), cacheDir)
            }
            if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
                appCacheDir = context.cacheDir
            }
            return appCacheDir
        }

        private fun hasExternalStoragePermission(context: Context): Boolean =
            PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}
