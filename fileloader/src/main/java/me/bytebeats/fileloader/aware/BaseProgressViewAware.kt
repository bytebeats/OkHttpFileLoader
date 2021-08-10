package me.bytebeats.fileloader.aware

import android.os.Looper
import android.view.View
import androidx.annotation.IntRange
import java.lang.ref.WeakReference

/**
 * Created by bytebeats on 2021/8/10 : 20:09
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
abstract class BaseProgressViewAware(private val view: View) : ProgressAware {
    protected val weaklyReachable = WeakReference(view)

    override fun id(): Int = weaklyReachable.get()?.hashCode() ?: super.hashCode()

    override fun isGCed(): Boolean = weaklyReachable.get() != null

    override fun setProgress(progress: Int): Boolean {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            weaklyReachable.get()?.let {
                setProgress(progress, it)
                return true
            }
        }
        return false
    }

    override fun wrappedView(): View? = weaklyReachable.get()

    override fun setVisibility(visibility: Int) {
        weaklyReachable.get()?.visibility = visibility
    }

    abstract fun setProgress(@IntRange(from = 0, to = 100) progress: Int, view: View)
}