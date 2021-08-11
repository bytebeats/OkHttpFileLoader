package me.bytebeats.fileloader.upload.preprocessor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Created by bytebeats on 2021/8/11 : 11:21
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class ImagePreProcessor(val cacheDir: File, val maxWidth: Int, val maxHeight: Int) : IPreProcessor {
    override fun preprocess(filePath: String): String? = smallImage(filePath)

    private fun smallImage(filePath: String): String? {
        try {
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val tmpFile = File(cacheDir, generatePhotoName())

            //compress files
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            val sampleSize =
                computeSampleSize(options, min(maxWidth, maxHeight), maxWidth * maxHeight)
            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize
            var bmp = BitmapFactory.decodeFile(filePath, options)
            //handle rotation
            val exifInfo = defineExifOrientation(filePath)
            if (exifInfo.rotation != 0 || exifInfo.flipHorizontal) {
                val matrix = Matrix()
                val flip = exifInfo.flipHorizontal
                val rotation = exifInfo.rotation
                if (flip) {
                    matrix.postScale(-1F, 1F)
                }
                if (rotation != 0) {
                    matrix.postRotate(rotation.toFloat())
                }
                val rotateBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
                if (bmp != rotateBmp) {
                    bmp.recycle()
                    bmp = null
                }
                bmp = rotateBmp
            }
            saveToFile(bmp, tmpFile)
            if (!bmp.isRecycled) {
                bmp.recycle()
            }
            return tmpFile.absolutePath
        } catch (ignored: Exception) {

        }
        return null
    }

    private fun generatePhotoName(): String {
        return "${System.currentTimeMillis()}-${Random(0).nextInt(1000)}"
    }

    companion object {
        private fun computeInitialSampleSize(
            options: BitmapFactory.Options,
            minSideLength: Int,
            maxPixels: Int
        ): Int {
            val w = options.outWidth
            val h = options.outHeight
            val lowerBound =
                if (maxPixels == -1) 1 else ceil(sqrt(w.toDouble() * h / maxPixels)).toInt()
            val upperBound = if (minSideLength == -1) 128 else min(
                floor(w.toDouble() / minSideLength),
                floor(h.toDouble() / minSideLength)
            ).toInt()
            if (upperBound < lowerBound) {
                return lowerBound//return the larger one when there is no overlapping zone
            }
            return if (maxPixels == -1 && minSideLength == -1) {
                1
            } else if (minSideLength == -1) {
                lowerBound
            } else {
                upperBound
            }
        }

        fun computeSampleSize(
            options: BitmapFactory.Options,
            minSideLength: Int,
            maxPixels: Int
        ): Int {
            val initialSize = computeInitialSampleSize(options, minSideLength, maxPixels)
            var roundedSize = 0
            if (initialSize <= 8) {
                roundedSize = 1
                while (roundedSize < initialSize) {
                    roundedSize = roundedSize shl 1
                }
            } else {
                roundedSize = (initialSize + 7) / 8 * 8
            }
            return roundedSize
        }

        private fun saveToFile(bmp: Bitmap, location: File): Boolean {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(location)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                return true
            } catch (ignored: IOException) {
                //ignored
            } finally {
                try {
                    fos?.close()
                } catch (ignored: IOException) {
                    //ignored
                }
            }
            return false
        }

        private fun defineExifOrientation(filePath: String): ExifInfo {
            var rotation = 0
            var flip = false
            try {
                val exifInterface = ExifInterface(filePath)
                val exifOrientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (exifOrientation) {
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip = true
                    ExifInterface.ORIENTATION_NORMAL -> rotation = 0
                    ExifInterface.ORIENTATION_TRANSVERSE -> flip = true
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip = true
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                    ExifInterface.ORIENTATION_TRANSPOSE -> flip = true
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                }
            } catch (ignored: IOException) {
                //ignored
            }
            return ExifInfo(rotation, flip)
        }
    }

    data class ExifInfo(val rotation: Int = 0, val flipHorizontal: Boolean = false)
}