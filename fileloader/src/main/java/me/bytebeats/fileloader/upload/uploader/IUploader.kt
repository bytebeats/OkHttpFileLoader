package me.bytebeats.fileloader.upload.uploader

import me.bytebeats.fileloader.upload.FileUploadInfo
import me.bytebeats.fileloader.upload.listener.OnTransferListener

/**
 * Created by bytebeats on 2021/8/11 : 20:49
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface IUploader<T> {
    fun upload(uploadInfo: FileUploadInfo<T>, transferredListener: OnTransferListener): String?
    fun cancel(uploadInfo: FileUploadInfo<T>)
}