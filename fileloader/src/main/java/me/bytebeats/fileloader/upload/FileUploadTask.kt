package me.bytebeats.fileloader.upload

import android.os.Handler
import android.os.Looper
import me.bytebeats.fileloader.aware.ProgressAware
import me.bytebeats.fileloader.upload.listener.OnTransferListener
import me.bytebeats.fileloader.upload.parser.ParsedResult

/**
 * Created by bytebeats on 2021/8/12 : 10:47
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileUploadTask(
    val engine: FileUploadEngine,
    val fileUploadInfo: FileUploadInfo<String>,
    val progressAware: ProgressAware,
    val mHandler: Handler
) : Runnable {

    @Volatile
    private var mProgressAware: ProgressAware? = null

    @Volatile
    private var mCancelled = false

    private var currentProgress = 0

    var isSyncLoading = false

    private val mOnTransferListener = object : OnTransferListener {
        var tmpTime = 0L
        override fun onTransferred(transferred: Long, totalSize: Long) {
            if (mCancelled) return
            val progress = (100 * transferred.toFloat() / totalSize).toInt()
            val now = System.currentTimeMillis()
            if (now - tmpTime > 100 || progress >= 100) {
                tmpTime = now
                fireProgressEvent(transferred, totalSize, progress)
            }
            currentProgress = progress
        }
    }

    override fun run() {
    }

    fun stop() {

    }

    fun resetProgressAware(progressAware: ProgressAware?) {
        mProgressAware = progressAware
        mProgressAware?.let {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                it.setProgress(currentProgress)
            } else {
                mHandler.post { it.setProgress(currentProgress) }
            }
        }
    }

    private fun fireProgressEvent(currentSize: Long, totalSize: Long, progress: Int) {
        if (fileUploadInfo.onUploadProgressListener == null && mProgressAware == null) {
            return
        }
        val task = Runnable {
            fileUploadInfo.onUploadProgressListener?.onProgress(progress, currentSize, totalSize)
            mProgressAware?.let {
                if (!it.isGCed() && !isProgressAwareReused(it)) {
                    it.setProgress(progress)
                }
            }
        }
        runTask(task, mHandler)
    }

    private fun fireSuccessEvent(parsedResult: ParsedResult<String>) {
        removeMyself()
        val task = Runnable {
            fileUploadInfo.onUploadListener?.onSucceed(fileUploadInfo, parsedResult.data)
            mProgressAware?.let { cancelUpdateProgressTask(it) }
        }
        runTask(task, null)
    }

    private fun isProgressAwareReused(progressAware: ProgressAware?): Boolean {
        return engine.fileUploadInfoIdForProgressAware(progressAware) != fileUploadInfo.id
    }

    private fun cancelUpdateProgressTask(progressAware: ProgressAware?) {
        progressAware?.let {
            if (it.isGCed()) {
                engine.cancelUpdateProgressTaskFor(it)
            } else {
                if (!isProgressAwareReused(it)) {
                    engine.cancelUpdateProgressTaskFor(it)
                }
            }
        }
    }

    private fun runTask(task: Runnable, handler: Handler?) {
        handler?.post(task) ?: run {
            if (isSyncLoading) {
                task.run()
            } else {
                mHandler.post(task)
            }
        }
    }

    private fun removeMyself() {
        engine.removeTask(this)
    }
}