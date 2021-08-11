package me.bytebeats.fileloader.upload.parser

/**
 * Created by bytebeats on 2021/8/11 : 12:07
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
abstract class ParsedResult<T>(private val data: T) {
    abstract fun isSuccessful(): Boolean
    abstract fun message(): String?
}