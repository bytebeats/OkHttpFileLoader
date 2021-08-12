package me.bytebeats.fileloader.upload

import me.bytebeats.fileloader.aware.ProgressAware
import java.util.*

/**
 * Created by bytebeats on 2021/8/12 : 10:45
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class FileUploadEngine(val fileUploadOptions: FileUploadOptions<String>) {
    private val mTaskExecutor = fileUploadOptions.mTaskExecutor

    private val mCacheKeysForProgressAware =
        Collections.synchronizedMap(mutableMapOf<Int, String>())

    private val mTasks = mutableListOf<FileUploadTask>()

    private val mLock = Any()

    fun submit(task: FileUploadTask) {
        addTask(task)
        mTaskExecutor?.execute(task)
    }

    private fun addTask(task: FileUploadTask) {
        synchronized(mLock) {
            mTasks.add(task)
        }
    }

    fun removeTask(task: FileUploadTask) {
        synchronized(mLock) {
            mTasks.remove(task)
        }
    }

    @Synchronized
    fun isTaskExists(id: String?, filePath: String, progressAware: ProgressAware?): Boolean {
        for (task in mTasks) {
            val info = task.fileUploadInfo
            if (id == info.id && info.originalFilePath() == filePath) {
                task.resetProgressAware(progressAware)
                return true
            }
        }
        return false
    }

    fun prepareUpdateProgressTaskFor(progressAware: ProgressAware, uploadInfoId: String) {
        mCacheKeysForProgressAware[progressAware.id()] = uploadInfoId
    }

    fun cancelUpdateProgressTaskFor(progressAware: ProgressAware) {
        mCacheKeysForProgressAware.remove(progressAware.id())
    }

    fun fileUploadInfoIdForProgressAware(progressAware: ProgressAware?): String? {
        return mCacheKeysForProgressAware[progressAware?.id()]
    }

    @Synchronized
    fun allTasks(): List<FileUploadTask> {
        return mTasks.toList()
    }

    @Synchronized
    fun taskCount(mimeType: String?): Int {
        if (mimeType.isNullOrEmpty()) return mTasks.size
        return mTasks.filter { it.fileUploadInfo.mimeType?.startsWith(mimeType) == true }.count()
    }

    @Synchronized
    fun stop() {
        for (task in mTasks) {
            task.stop()
        }
        mCacheKeysForProgressAware.clear()
    }

}