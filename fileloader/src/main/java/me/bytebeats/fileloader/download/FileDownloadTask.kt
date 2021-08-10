package me.bytebeats.fileloader.download

import android.os.Handler
import androidx.annotation.IntRange
import me.bytebeats.fileloader.FileLoaderManager
import me.bytebeats.fileloader.aware.ProgressAware
import me.bytebeats.fileloader.download.listener.OnDownloadListener
import me.bytebeats.fileloader.download.listener.OnDownloadProgressListener
import okhttp3.Request
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by bytebeats on 2021/8/10 : 17:53
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileDownloadTask(
    val downloadInfo: FileDownloadInfo,
    private val downloadManager: FileDownloadManager,
    private var progressAware: ProgressAware?
) : Runnable {
    private var downloadListener: OnDownloadListener? = null
    private var progressListener: OnDownloadProgressListener? = null

    var isSyncLoading = false
    private var downloadedSize = 0L
    private var totalSize = 0L

    init {
        downloadListener = downloadInfo.onDownloadListener
        progressListener = downloadInfo.onProgressListener
    }

    fun resetProgressAware(progressAware: ProgressAware?, handler: Handler) {
        this.progressAware = progressAware
        val t = if (totalSize == 0L) Int.MAX_VALUE.toLong() else totalSize
        val progress = (100.0 * downloadedSize / t).toInt()
        handler.post { this.progressAware?.setProgress(progress) }
    }

    private fun generateTag(downloadInfo: FileDownloadInfo): String {
        return "${downloadInfo.id}${downloadInfo.url.hashCode()}"
    }

    private fun isProgressAwareReused(progressAware: ProgressAware): Boolean =
        downloadInfo.id != downloadManager.getFileDownloadInfoIdFromProgressAware(progressAware)

    fun updateProgress(@IntRange(from = 0, to = 100) progress: Int) {
        progressAware?.let {
            if (!it.isGCed() && !isProgressAwareReused(it)) {
                it.setProgress(progress)
            }
        }
    }

    override fun run() {
        var request: Request? = null
        try {
            request = Request.Builder().url(downloadInfo.url).tag(generateTag(downloadInfo)).build()
        } catch (ignore: Exception) {
            downloadListener?.onFailed(this, ErrorType.INVALID_URL, ignore.message)
            return
        }
        try {
            val resp = FileLoaderManager.getInstance().mClientProxy.execute(request)
            if (resp.isSuccessful) {
                val body = resp.body ?: throw Exception("Response Body is null")
                val contentLength = body.contentLength()
                val byteStream = body.byteStream()
                val fos = FileOutputStream(downloadInfo.location)
                val buffer = ByteArray(1024)
                var currentSize = 0
                var size = byteStream.read(buffer)
                while (size != -1) {
                    fos.write(buffer, 0, size)
                    currentSize += size
                    this.downloadedSize = currentSize.toLong()
                    this.totalSize = contentLength
                    progressListener?.onProgress(this, downloadedSize, totalSize)
                    size = byteStream.read(buffer)
                }
                byteStream.close()
                fos.close()
                downloadListener?.onSucceed(this, downloadInfo.location)
            } else {
                downloadListener?.onFailed(this, ErrorType.OTHER, resp.message)
            }
        } catch (ignore: IOException) {
            downloadListener?.onFailed(this, ErrorType.NETWORK, ignore.message)
        } catch (ignore: Exception) {
            downloadListener?.onFailed(this, ErrorType.NETWORK, ignore.message)
        }
        progressAware?.let {
            downloadManager.cancelUpdateProgressTaskFor(it)
        }
    }
}