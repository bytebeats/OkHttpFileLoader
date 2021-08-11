package me.bytebeats.fileloader.upload.uploader

import me.bytebeats.fileloader.FileLoaderManager
import me.bytebeats.fileloader.upload.FileUploadInfo
import me.bytebeats.fileloader.upload.listener.OnTransferListener
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

/**
 * Created by bytebeats on 2021/8/11 : 21:00
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class OkHttpUploader<T>() : IUploader<T> {
    override fun upload(
        uploadInfo: FileUploadInfo<T>,
        transferredListener: OnTransferListener
    ): String? {
        val file = File(uploadInfo.uploadFilePath())
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        uploadInfo.params?.entries?.forEach { entry ->
            builder.addFormDataPart(entry.key, entry.value)
        }
        val mimeType = uploadInfo.mimeType ?: ""
        builder.addPart(
            Headers.headersOf(
                "Content-Disposition",
                "form-data; name=\"file\"; filename=\"${file.name}\""
            ), file.asRequestBody(mimeType.toMediaTypeOrNull())
        )
        val multipartBody = builder.build()
        val requestBody = ProgressRequestBody(multipartBody, transferredListener)
        val request = Request.Builder()
            .tag(generateTag(uploadInfo))
            .url(uploadInfo.url)
            .header("Content-Type", uploadInfo.mimeType ?: "")
            .post(requestBody)
            .build()
        val response = FileLoaderManager.getInstance().mClientProxy.execute(request)
        return if (response.isSuccessful) {
            response.body?.string()
        } else throw IOException(response.message)
    }

    override fun cancel(uploadInfo: FileUploadInfo<T>) {
        FileLoaderManager.cancelRequest(generateTag(uploadInfo))
    }

    private fun generateTag(uploadInfo: FileUploadInfo<T>): String {
        return "${uploadInfo.id}${uploadInfo.uploadFilePath().hashCode()}"
    }
}