package me.bytebeats.fileloader

import me.bytebeats.fileloader.upload.parser.IResponseParser
import me.bytebeats.fileloader.upload.parser.ParsedResult
import me.bytebeats.fileloader.upload.parser.StringResponseParser
import me.bytebeats.fileloader.upload.uploader.IUploader
import me.bytebeats.fileloader.upload.uploader.OkHttpUploader
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by bytebeats on 2021/8/10 : 19:40
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
object DefaultOptionsFactory {
    fun createExecutor(poolSize: Int, threadPriority: Int): ExecutorService = ThreadPoolExecutor(
        poolSize,
        poolSize,
        30,
        TimeUnit.MILLISECONDS,
        LinkedBlockingDeque(),
        createDefaultThreadFactory(threadPriority)
    )

    private fun createDefaultThreadFactory(priority: Int): DefaultThreadFactory =
        DefaultThreadFactory(priority)

    fun <T> createDefaultUploader(): IUploader<T> = OkHttpUploader()

    fun <T> createDefaultResponseProcessor(): IResponseParser<T> = object : IResponseParser<T> {
        override fun parse(response: T): ParsedResult<T> {
            return object : ParsedResult<T>(response) {
                override fun isSuccessful(): Boolean = false
                override fun message(): String? = null
            }
        }
    }

    private class DefaultThreadFactory(private val priority: Int) : ThreadFactory {
        private val threadGroup by lazy { Thread.currentThread().threadGroup }
        private val threadNumber by lazy { AtomicInteger(1) }
        private val namePrefix by lazy { "fileload=${THREAD_POOL_NUMBER.getAndIncrement()}-thread-" }

        override fun newThread(r: Runnable?): Thread {
            val thread = Thread(threadGroup, r, "$namePrefix${threadNumber.getAndIncrement()}")
            thread.priority = priority
            if (thread.isDaemon) {
                thread.isDaemon = false
            }
            return thread
        }

        companion object {
            private val THREAD_POOL_NUMBER = AtomicInteger(1)
        }
    }
}