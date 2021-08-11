package me.bytebeats.fileloader.upload.uploader

import me.bytebeats.fileloader.upload.listener.OnTransferListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*

/**
 * Created by bytebeats on 2021/8/11 : 20:52
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val transferredListener: OnTransferListener? = null
) : RequestBody() {
    private var mBufferedSink: BufferedSink? = null

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun contentLength(): Long = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        if (mBufferedSink == null) {
            mBufferedSink = sink(sink).buffer()
        }
        mBufferedSink?.let {
            requestBody.writeTo(it)
            it.flush()
        }

    }

    private fun sink(sink: Sink): Sink = object : ForwardingSink(sink) {
        private var contentLength = 0L
        private var writtenBytes = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            if (contentLength == 0L) {
                contentLength = contentLength()
            }
            writtenBytes += byteCount
            transferredListener?.onTransferred(writtenBytes, contentLength)
        }
    }
}