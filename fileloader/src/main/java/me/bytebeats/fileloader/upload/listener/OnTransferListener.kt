package me.bytebeats.fileloader.upload.listener

/**
 * Created by bytebeats on 2021/8/11 : 11:59
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface OnTransferListener {
    fun onTransferred(transferred: Long, totalSize: Long)
}