package com.spidev.materialimagecropper

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.Toast
import java.io.IOException

/**
 * Created by carlos on 12/2/17.
 */
class ImagesUtil {

    companion object {

        @RequiresApi(Build.VERSION_CODES.N)
        fun getImageOrientation(context: Context, uri: Uri?): Int {
            val inputStream = context.contentResolver.openInputStream(uri)
            try {
                val exifInterface = ExifInterface(inputStream)
                return getImageOrientation(exifInterface)
            } catch (ioException: IOException) {

            }
            return 0
        }

        private fun getImageOrientation(exifInterface: ExifInterface): Int {
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

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
        }
    }
}