package me.bytebeats.fileloader.upload

import android.content.Context
import me.bytebeats.fileloader.DefaultOptionsFactory
import me.bytebeats.fileloader.StorageUtil
import me.bytebeats.fileloader.upload.parser.IResponseParser
import me.bytebeats.fileloader.upload.uploader.IUploader
import java.util.concurrent.Executor

/**
 * Created by bytebeats on 2021/8/10 : 19:26
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileUploadOptions<T> private constructor(builder: Builder<T>) {
    private val mContext = builder.context
    val mTaskExecutor = builder.taskExecutor
    private val mUsingInjectedExecutor = builder.usingInjectedExecutor
    val mUploader = builder.uploader
    val mResponseParser = builder.responseParser

    class Builder<T>(
        val context: Context,
        var taskExecutor: Executor? = null,
        var uploader: IUploader<T>? = null,
        var responseParser: IResponseParser<T>? = null,
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

        fun build(): FileUploadOptions<T> {
            ensureParametersValid()
            return FileUploadOptions(this)
        }

        private fun ensureParametersValid() {
            if (taskExecutor == null) {
                taskExecutor = DefaultOptionsFactory.createExecutor(threadPoolSize, threadPriority)
            } else {
                usingInjectedExecutor = true
            }
            if (uploader == null) {
                uploader = DefaultOptionsFactory.createDefaultUploader()
            }
            if (responseParser == null) {
                responseParser = DefaultOptionsFactory.createDefaultResponseProcessor()
            }
        }
    }
}
