package com.spidev.mandarinfish.util

import android.content.Context
import android.support.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by carlos on 12/2/17.
 */
class ImagesUtil {

    companion object {

        fun createPublicImageFile(): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"

            //Using the Picture public directory, accessible by all apps
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            Log.e("X-PUBLICSTORAGEDIR", "X-PUBLICSTORAGEDIR " + storageDir)
            val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

            /* //Save a file: path for use with ACTION_VIEW intents
             mCurrentPhotoPath = imageFile.absolutePath
 */
            return imageFile
        }

        fun createPrivateImageFile(context: Context): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"

            //Using the Picture public directory, accessible by all apps
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            Log.e("X-PRIVATESTORAGEDIR", "X-PRIVATESTORAGEDIR " + storageDir)
            val image = File.createTempFile(imageFileName, ".jpg", storageDir)

            /*//Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.absolutePath*/

            return image
        }


        private fun getInputStreamFromUri(context: Context, uri: Uri): InputStream = context.contentResolver.openInputStream(uri)

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

        fun showExifTag(context: Context, uri: Uri) {

            val inputStream = context.contentResolver.openInputStream(uri)
            val exifInterface = ExifInterface(inputStream)
            val imageLength = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
            val imageWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
            val dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val make = exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            val model = exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE)
            val focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH)
            val gpsDatestamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)
            val gpsTimestamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
            val latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            val latitudeRed = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
            val longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
            val longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
            val gpsProcessingMethod = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)
            var latLong0 = 0.0
            var latLong1 = 0.0
            if (exifInterface.latLong != null && exifInterface.latLong.size == 2) {
                latLong0 = exifInterface.latLong[0]
                latLong1 = exifInterface.latLong[1]
            }

            val exif = "ExifInterface Information: " +
                    "\n imageLength: $imageLength " +
                    "\n imageWidth: $imageWidth " +
                    "\n dateTime: $dateTime " +
                    "\n make: $make " +
                    "\n model: $model " +
                    "\n orientation: $orientation " +
                    "\n whiteBalance: $whiteBalance " +
                    "\n focalLength: $focalLength " +
                    "\n flash: $flash " +
                    "\n gpsDatestamp: $gpsDatestamp " +
                    "\n gpsTimestamp: $gpsTimestamp " +
                    "\n latitude: $latitude " +
                    "\n latitudeRed: $latitudeRed " +
                    "\n longitude: $longitude " +
                    "\n longitudeRef: $longitudeRef " +
                    "\n gpsProcessingMethod: $gpsProcessingMethod " +
                    "\n latLong0: $latLong0 " +
                    "\n latLong1: $latLong1 "

            Toast.makeText(context, exif, Toast.LENGTH_LONG).show()
        }
    }
}