package me.bytebeats.fileloader.upload.listener

import me.bytebeats.fileloader.upload.FileUploadInfo
import me.bytebeats.fileloader.upload.UploadErrorType

/**
 * Created by bytebeats on 2021/8/11 : 12:00
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface OnUploadListener<T> {
    fun onSucceed(info: FileUploadInfo, data: T)
    fun onError(info: FileUploadInfo, errorType: UploadErrorType, message: String?)
}