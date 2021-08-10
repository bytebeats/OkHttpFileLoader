package me.bytebeats.fileloader.download.listener

import me.bytebeats.fileloader.download.FileDownloadTask

/**
 * Created by bytebeats on 2021/8/10 : 17:52
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface OnDownloadProgressListener {
    fun onProgress(task: FileDownloadTask, currentLength: Long, totalLength: Long)
}