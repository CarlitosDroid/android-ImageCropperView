package com.spidev.materialimagecropper

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author carlosleonardocamilovargashuaman on 7/6/18.
 */
class FileUtils {

    companion object {

        fun getImagesFolderPath() =
                "${Environment.getExternalStorageDirectory().absolutePath}/${Environment.DIRECTORY_DCIM}/ImageCropperImages"


        fun saveToFile(isDirectory: Boolean, bitmap: Bitmap): String {
            val imageFile: File

            val folder = File(getImagesFolderPath())
            val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val imageName = "${simpleDateFormat.format(Date())}.jpg"

            var folderAlreadyExist = false
            var folderCreated = false
            if (folder.exists()) {
                folderAlreadyExist = true
            } else {
                folderCreated = folder.mkdir()
            }

            Log.e("FOLDER ALREADY ", "EXIST $folderAlreadyExist")
            Log.e("FOLDER WAS ", "CREATED $folderCreated")
            imageFile = File(folder, imageName)
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
            fileOutputStream.close()
            return imageFile.path
        }
    }
}