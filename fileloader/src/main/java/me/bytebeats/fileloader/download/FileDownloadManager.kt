package me.bytebeats.fileloader.download

import android.content.Context
import android.os.Handler
import android.os.Looper
import me.bytebeats.fileloader.aware.ProgressAware
import me.bytebeats.fileloader.download.listener.OnDownloadListener
import me.bytebeats.fileloader.download.listener.OnDownloadProgressListener
import java.io.File
import java.util.*

/**
 * Created by bytebeats on 2021/8/10 : 20:23
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileDownloadManager private constructor(context: Context) {

    private val mContext = context.applicationContext
    private var mOptions: DownloadOptions? = null
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    private val mTasks = mutableListOf<FileDownloadTask>()
    private val mDownloadListeners =
        Collections.synchronizedMap(mutableMapOf<FileDownloadTask, OnDownloadListener>())
    private val mProgressListeners =
        Collections.synchronizedMap(mutableMapOf<FileDownloadTask, OnDownloadProgressListener>())
    private val mCachedKeysForProgressAware =
        Collections.synchronizedMap(mutableMapOf<Int, String>())

    private val mProgressDispatcher by lazy {
        object : OnDownloadProgressListener {
            override fun onProgress(
                task: FileDownloadTask,
                currentLength: Long,
                totalLength: Long
            ) {
                mProgressListeners[task]?.let { listener ->
                    val t = if (totalLength == 0L) Int.MAX_VALUE.toLong() else totalLength
                    val progress = (100.0 * currentLength / totalLength).toInt()
                    mainHandler.post {
                        task.updateProgress(progress)
                        listener.onProgress(task, currentLength, totalLength)
                    }
                }

            }
        }
    }

    private val mDownloadDispatcher by lazy {
        object : OnDownloadListener {
            override fun onFailed(task: FileDownloadTask, errorType: ErrorType, message: String?) {

            }

            override fun onSucceed(task: FileDownloadTask, location: File) {

            }
        }
    }

    @Synchronized
    fun init(options: DownloadOptions) {
        mOptions = options
    }

    private fun ensureOptions() {
        mOptions ?: throw IllegalStateException("Please call init(options) before use.")
    }

    private fun isTaskExists(id: String?, url: String): Boolean {
        if (!id.isNullOrEmpty()) {
            for (task in mTasks) {
                val downloadInfo = task.downloadInfo
                if (id == downloadInfo.id && url == downloadInfo.url) {
                    return true
                }
            }
        }
        return false
    }

    fun download(
        type: FileType,
        id: String?,
        url: String,
        progressAware: ProgressAware?,
        onDownloadListener: OnDownloadListener?,
        onProgressListener: OnDownloadProgressListener?
    ) {
        ensureOptions()
        synchronized(mTasks) {
            if (isTaskExists(id, url)) return
            val cacheFile = generateCacheFile(url, type)
            val downloadInfo =
                FileDownloadInfo(id!!, url, cacheFile, mDownloadDispatcher, mProgressDispatcher)
            val task = FileDownloadTask(downloadInfo, this, progressAware)
            mTasks.add(task)
            onDownloadListener?.let { mDownloadListeners[task] = it }
            onProgressListener?.let { mProgressListeners[task] = it }
            // TODO: 2021/8/10 progressaware
            mOptions?.mTaskExecutor?.execute(task)
        }
    }

    fun getFileDownloadInfoIdFromProgressAware(progressAware: ProgressAware): String? {
        return ""
    }

    fun cancelUpdateProgressTaskFor(progressAware: ProgressAware) {

    }

    private fun generateCacheFile(url: String, type: FileType): File {
        var cacheDir = mOptions!!.mCacheDir
        when (type) {
            FileType.AUDIO -> cacheDir = File("${cacheDir?.absolutePath}${File.separator}audio")
            FileType.VIDEO -> cacheDir = File("${cacheDir?.absolutePath}${File.separator}video")
            FileType.IMAGE -> cacheDir = File("${cacheDir?.absolutePath}${File.separator}image")
        }
        if (cacheDir?.exists() != true) {
            cacheDir?.mkdirs()
        }
        val name = generateCacheName(url)
        return File(cacheDir, name)
    }

    private fun generateCacheName(url: String): String {
        return "${url.hashCode()}_${System.currentTimeMillis()}"
    }

    companion object {
        private var instance: FileDownloadManager? = null
        fun getInstance(context: Context): FileDownloadManager {
            if (instance == null) {
                instance = FileDownloadManager(context)
            }
            return instance!!
        }
    }
}