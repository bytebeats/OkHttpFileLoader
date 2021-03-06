package me.bytebeats.fileloader.download

import android.content.Context
import me.bytebeats.fileloader.DefaultOptionsFactory
import me.bytebeats.fileloader.StorageUtil
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
        private var threadPoolSize: Int = StorageUtil.DEFAULT_THREAD_POOS_SIZE,
    ) {
        var threadPriority: Int = StorageUtil.DEFAULT_THREAD_PRIORITY
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
                cacheDir = StorageUtil.getOwnCacheDirectory(context, "Downloads")
            }
        }
    }
}
