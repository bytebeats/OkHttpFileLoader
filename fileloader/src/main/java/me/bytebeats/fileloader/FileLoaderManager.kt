package me.bytebeats.fileloader

/**
 * Created by bytebeats on 2021/8/10 : 16:57
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileLoaderManager {

    val mClientProxy by lazy { FileLoaderClientProxy() }

    companion object {
        private var manager: FileLoaderManager? = null

        fun getInstance(): FileLoaderManager {
            if (manager == null) {
                manager = FileLoaderManager()
            }
            return manager!!
        }
    }
}