package me.bytebeats.fileloader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File

/**
 * Created by bytebeats on 2021/8/11 : 20:46
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
object StorageUtil {
    const val DEFAULT_THREAD_POOS_SIZE = 3
    const val DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1

    fun getOwnCacheDirectory(context: Context, cacheDir: String): File? {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            && hasExternalStoragePermission(context)
        ) {
            appCacheDir = File(Environment.getExternalStorageDirectory(), cacheDir)
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir
    }

    private fun hasExternalStoragePermission(context: Context): Boolean =
        PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}