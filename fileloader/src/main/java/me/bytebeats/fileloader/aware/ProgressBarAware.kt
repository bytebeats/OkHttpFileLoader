package me.bytebeats.fileloader.aware

import android.view.View
import android.widget.ProgressBar

/**
 * Created by bytebeats on 2021/8/10 : 20:17
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class ProgressBarAware(view: ProgressBar) : BaseProgressViewAware(view) {
    override fun setProgress(progress: Int, view: View) {
        (view as ProgressBar).progress = progress
    }
}