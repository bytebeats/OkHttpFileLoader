package me.bytebeats.fileloader.upload

import me.bytebeats.fileloader.upload.parser.IResponseParser
import me.bytebeats.fileloader.upload.preprocessor.IPreProcessor

/**
 * Created by bytebeats on 2021/8/11 : 11:18
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class UploadOptions<T> private constructor(private val builder: Builder<T>) {
    val mPreprocessor = builder.preprocessor
    val mResponseParser = builder.responseParser

    class Builder<T>(
        val preprocessor: IPreProcessor? = null,
        val responseParser: IResponseParser<T>? = null
    ) {
        fun build(): UploadOptions<T> {
            return UploadOptions(this)
        }
    }

}
