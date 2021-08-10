package me.bytebeats.fileloader.download.listener

import me.bytebeats.fileloader.download.ErrorType
import me.bytebeats.fileloader.download.FileDownloadTask
import java.io.File

/**
 * Created by bytebeats on 2021/8/10 : 19:16
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface OnDownloadListener {
    fun onSucceed(task: FileDownloadTask, location: File)
    fun onFailed(task: FileDownloadTask, errorType: ErrorType, message: String?)
}