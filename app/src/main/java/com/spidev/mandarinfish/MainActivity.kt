package com.spidev.mandarinfish

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fabCameraSavePublicImage.setOnClickListener { _ ->
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                //Create the FIle where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createPublicImageFile()
                    /** For using createPrivateImageFile() method you only have to change the path in xml/file_paths to your
                     * private public directory returned by getExternalFilesDir method
                     */
                } catch (e: IOException) {
                    // Error ocurred while creating a file

                }

                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID,
                            photoFile)

                    Log.e("X-PHOTOURI", "X-PHOTOURI " + photoURI)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TO_CAMERA_GET_FULL_SIZE_IMAGE)
                }
            }
        }

        fabGallery.setOnClickListener { _ ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQUEST_TO_MEDIA)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TO_MEDIA -> if (resultCode == Activity.RESULT_OK) {

                Log.e("DATA-MEDIA", "" + data?.data)

                //mInstaCropper.setIm(data?.data)

            } else if (resultCode == Activity.RESULT_CANCELED) {

            }

            REQUEST_TO_CAMERA_GET_FULL_SIZE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                setPic();
                //mInstaCropper.setIm(data?.data)

            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    companion object {
        const val REQUEST_TO_CAMERA_GET_FULL_SIZE_IMAGE: Int = 1
        const val REQUEST_TO_MEDIA: Int = 2

    }

    fun createPublicImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        //Using the Picture public directory, accessible by all apps
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        Log.e("X-PUBLICSTORAGEDIR", "X-PUBLICSTORAGEDIR " + storageDir)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        //Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath

        return image
    }

    fun createPrivateImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

        //Using the Picture public directory, accessible by all apps
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.e("X-PRIVATESTORAGEDIR", "X-PRIVATESTORAGEDIR " + storageDir)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        //Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath

        return image
    }

    fun setPic() {
        //Get the dimensions of the View
        val targetW: Int = imgPicture.width
        val targetH: Int = imgPicture.height

        //Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW: Int = bmOptions.outWidth
        val photoH: Int = bmOptions.outHeight

        //Determine how much to scale down the image
        var scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

        //Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = 2

        Log.e("X-scaleFactor", "" + scaleFactor)
        Log.e("X-mCurrentPhotoPath", "" + mCurrentPhotoPath)
        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)

        imgPicture.setImageBitmap(bitmap)
    }
}
