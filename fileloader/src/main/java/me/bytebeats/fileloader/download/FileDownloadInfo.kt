package me.bytebeats.fileloader.download

import me.bytebeats.fileloader.download.listener.OnDownloadListener
import me.bytebeats.fileloader.download.listener.OnDownloadProgressListener
import java.io.File

/**
 * Created by bytebeats on 2021/8/10 : 19:15
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
data class FileDownloadInfo(
    var id: String,
    var url: String,
    val location: File,
    var onDownloadListener: OnDownloadListener?,
    var onProgressListener: OnDownloadProgressListener?
)
