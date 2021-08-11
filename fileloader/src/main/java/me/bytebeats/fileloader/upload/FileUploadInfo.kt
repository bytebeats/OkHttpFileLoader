package me.bytebeats.fileloader.upload

import me.bytebeats.fileloader.upload.listener.OnUploadListener
import me.bytebeats.fileloader.upload.listener.OnUploadProgressListener

/**
 * Created by bytebeats on 2021/8/11 : 11:17
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
data class FileUploadInfo<T>(
    val params: Map<String, String>?,
    val id: String,
    val filePath: String,
    val mimeType: String,
    val url: String,
    val options: UploadOptions<T>? = null,
    val onUploadListener: OnUploadListener<T>? = null,
    val onUploadProgressListener: OnUploadProgressListener? = null
)
