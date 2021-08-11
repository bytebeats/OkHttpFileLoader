package me.bytebeats.fileloader.upload.parser

import kotlin.jvm.Throws

/**
 * Created by bytebeats on 2021/8/11 : 12:10
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface IResponseParser<T> {
    @Throws(Exception::class)
    fun parse(response: T): ParsedResult<T>
}