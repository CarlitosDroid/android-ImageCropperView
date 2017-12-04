package com.spidev.materialimagecropper

import android.content.Context
import android.support.media.ExifInterface
import android.net.Uri
import java.io.IOException

/**
 * Created by carlos on 12/2/17.
 */
class ImagesUtil {

    companion object {
        fun getImageOrientation(context: Context, uri: Uri?): Int {
            val inputStream = context.contentResolver.openInputStream(uri)
            try {
                val exifInterface = ExifInterface(inputStream)
                return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } catch (ioException: IOException) {

            }
            return 0
        }

        fun getImageRotation(context: Context, uri: Uri?): Int {
            try {
                val orientation = getImageOrientation(context, uri)
                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->
                        90
                    ExifInterface.ORIENTATION_ROTATE_180 ->
                        180
                    ExifInterface.ORIENTATION_ROTATE_270 ->
                        270
                    ExifInterface.ORIENTATION_NORMAL ->
                        0
                    else -> 0
                }
            } catch (ioException: IOException) {

            }
            return 0
        }
    }
}