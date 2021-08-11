package me.bytebeats.fileloader.upload.listener

/**
 * Created by bytebeats on 2021/8/11 : 12:05
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface OnUploadProgressListener {
    fun onProgress(progress: Int, uploadedSize: Long, totalSize: Long)
}