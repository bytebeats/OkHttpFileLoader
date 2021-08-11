package me.bytebeats.fileloader.upload.parser

/**
 * Created by bytebeats on 2021/8/11 : 12:11
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class StringResponseParser : IResponseParser<String> {
    /**
     * do nothing by default
     */
    override fun parse(response: String): ParsedResult<String> {
        return object : ParsedResult<String>(response) {
            override fun isSuccessful(): Boolean = false
            override fun message(): String? = null
        }
    }
}