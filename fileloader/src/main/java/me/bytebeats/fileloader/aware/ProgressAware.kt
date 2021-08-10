package me.bytebeats.fileloader.aware

import android.view.View
import androidx.annotation.IntRange

/**
 * Created by bytebeats on 2021/8/10 : 20:06
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
interface ProgressAware {
    fun id(): Int
    fun isGCed(): Boolean
    fun setProgress(@IntRange(from = 0, to = 100) progress: Int): Boolean
    fun setVisibility(visibility: Int)
    fun wrappedView(): View?
}