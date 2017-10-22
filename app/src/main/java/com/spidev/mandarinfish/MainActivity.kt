package com.spidev.mandarinfish


import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.spidev.mandarinfish.activities.MaterialImageCropperActivity
import com.spidev.mandarinfish.commons.Constants
import com.spidev.mandarinfish.fragments.CameraDialogFragment
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: Int = 1

class MainActivity : AppCompatActivity(), CameraDialogFragment.OnCameraRationaleListener {
    override fun onAccept() {
        requestThePermissions()
    }

    var mCurrentPhotoPath: String = ""
    var cameraDialogFragment: CameraDialogFragment? = null

    private var galleryImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fabCameraSavePublicImage.setOnClickListener { _ ->
            getRequestPermission()
        }

        fabGallery.setOnClickListener { _ ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQUEST_TO_MEDIA)
        }

        fabNextActivity.setOnClickListener { _ ->
            if (galleryImageUri != null) {
                val destinationUri = Uri.fromFile(File(externalCacheDir, "test.jpg"))
                startMaterialImageCropperActivity(galleryImageUri!!, destinationUri)
            } else {
                Toast.makeText(this, "Select a Picture from Gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMaterialImageCropperActivity(sourceUri: Uri, destinationUri: Uri) {
        val intent = Intent(this, MaterialImageCropperActivity::class.java)
        intent.data = sourceUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destinationUri)
        intent.putExtra(Constants.EXTRA_PREFERRED_RATIO, Constants.DEFAULT_RATIO)
        intent.putExtra(Constants.EXTRA_MINIMUN_RATIO, Constants.DEFAULT_MINIMUN_RATIO)
        intent.putExtra(Constants.EXTRA_MAXIMUN_RATIO, Constants.DEFAULT_MAXIMUN_RATIO)
        intent.putExtra(Constants.EXTRA_WIDTH_SPECIFICATION, View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.AT_MOST))
        intent.putExtra(Constants.EXTRA_HEIGHT_SPECIFICATION, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        intent.putExtra(Constants.EXTRA_OUTPUT_QUALITY, 50)
        startActivity(intent)
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
                //data?.data is not null when selecting any file from gallery
                //mInstaCropper.setImageUri(data?.data!!)
                Log.e("Gallery Image Uri", "data ${data?.data}")
                galleryImageUri = data?.data
                imgPhoto.setImageURI(data?.data)
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }

            REQUEST_TO_CAMERA_GET_FULL_SIZE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                //data?.data!! is null when returning from any Camera Application
                setPic()

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
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        //Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.absolutePath

        return imageFile
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
        val targetW: Int = imgPhoto.width
        val targetH: Int = imgPhoto.height

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

        imgPhoto.setImageBitmap(bitmap)

    }

    fun getRequestPermission() {
        Log.e("z-getRequestPermis***", "z-getRequestPermis***")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e("z-notPermissionWES", "z-notPermissionWES")

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("z-showDialogRationale", "z-showDialogRationale")

                val fragmentManager = this.supportFragmentManager
                cameraDialogFragment = CameraDialogFragment.newInstance("MOSTRAR MODAL")
                cameraDialogFragment?.show(fragmentManager, "layout_camera_layout")
            } else {

                Log.e("z-notNeedShowDialogRat", "z-notNeedShowDialogRat")

                // No explanation needed, we can request the permission.
                requestThePermissions()
            }
        } else {
            Log.e("z-permissionGrantedWES", "z-permissionGrantedWES")
            intentToImageCapture()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.e("z-onRequestPermResul***", "z-onRequestPermResul***")
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                Log.e("z-permissionGranted", "z-permissionGranted")

                cameraDialogFragment?.dismiss()

                intentToImageCapture()
            } else {
                Log.e("z-permissionNotGranted", "z-permissionNotGranted")
            }
        }
    }

    fun requestThePermissions() {
        val permissionArrayString = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        ActivityCompat.requestPermissions(this,
                permissionArrayString,
                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
    }

    fun intentToImageCapture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            //Create the File where the photo should go
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
}
