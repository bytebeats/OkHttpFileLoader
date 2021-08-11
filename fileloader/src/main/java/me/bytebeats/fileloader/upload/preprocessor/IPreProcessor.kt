package me.bytebeats.fileloader.upload.preprocessor

/**
 * Created by bytebeats on 2021/8/11 : 11:19
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface IPreProcessor {
    /**
     * In case extra work needs to be done before uploading
     */
    fun preprocess(filePath: String): String?
}