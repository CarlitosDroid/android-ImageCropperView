package com.spidev.mandarinfish.activities

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
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.spidev.mandarinfish.BuildConfig
import com.spidev.mandarinfish.R
import com.spidev.mandarinfish.commons.Constants
import com.spidev.mandarinfish.fragments.AppSettingsDialogFragment
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), AppSettingsDialogFragment.OnCameraRationaleListener {

    override fun onAccept() {
        openAppSettings()
    }

    companion object {
        const val REQUEST_TO_CAMERA_GET_FULL_SIZE_IMAGE: Int = 1
        const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: Int = 1
        const val REQUEST_TO_MEDIA: Int = 2

        const val INDEX_WRITE_PERMISSION = 0
        const val INDEX_READ_PERMISSION = 1
    }

    private var mCurrentPhotoPath: String = ""

    private var galleryImageUri: Uri? = null
    private var showCustomWritePermissionDialog = false
    private var showCustomReadPermissionDialog = false

    private var permissionsList = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        settingUpClickListener()
    }

    private fun settingUpClickListener() {
        fabCamera.setOnClickListener { _ ->
            validatePermission(permissionsList[INDEX_WRITE_PERMISSION])
        }

        fabGallery.setOnClickListener { _ ->
            validatePermission(permissionsList[INDEX_READ_PERMISSION])
        }

        fabNextActivity.setOnClickListener { _ ->
            if (galleryImageUri != null) {
                val destinationUri = Uri.fromFile(File(externalCacheDir, "test.jpg"))
                startMaterialImageCropperActivity(galleryImageUri!!, destinationUri)
                //startTestActivity(galleryImageUri!!, destinationUri)
            } else {
                Toast.makeText(this, "Select a Picture from Gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Validate the necessary permission for writing and reading files from storage or external applications
     * since we can open an external camera or gallery application but we cannot read or write the pictures or files
     * in current application.
     */
    private fun validatePermission(manifestPermission: String) {
        when (manifestPermission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                if (ContextCompat.checkSelfPermission(this, manifestPermission)
                        == PackageManager.PERMISSION_GRANTED) {
                    intentToImageCapture()
                } else {
                    if (showCustomReadPermissionDialog) {
                        showAppSettingsDialogFragment("Storage 0")
                    } else {
                        requestPermissions()
                    }
                }
            }
            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                if (ContextCompat.checkSelfPermission(this, manifestPermission)
                        == PackageManager.PERMISSION_GRANTED) {
                    openGalleryExternalApp()
                } else {
                    if (showCustomWritePermissionDialog) {
                        showAppSettingsDialogFragment("Storage 1")
                    } else {
                        requestPermissions()
                    }
                }
            }
        }
    }

    /**
     * Request and show a permissions modal
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                permissionsList.toTypedArray(),
                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
    }

    /**
     * Show custom dialog if user checks 'Don't ask again'
     */
    private fun openGalleryExternalApp() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, REQUEST_TO_MEDIA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                for (i in permissions.indices) {
                    when (permissions[i]) {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                intentToImageCapture()
                            } else {
                                // shouldShowRequestPermissionRationale return false if the user check "Don't ask again" or "Permission disabled"
                                // for more information https://youtu.be/C8lUdPVSzDk?t=2m23s
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                    showCustomWritePermissionDialog = true
                                }
                            }
                        }

                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                openGalleryExternalApp()
                            } else {
                                // shouldShowRequestPermissionRationale return false if the user check "Don't ask again" or "Permission disabled"
                                // for more information https://youtu.be/C8lUdPVSzDk?t=2m23s
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                    showCustomReadPermissionDialog = true
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TO_MEDIA -> if (resultCode == Activity.RESULT_OK) {
                //data?.data is NOT NULL when selecting any file from gallery
                //mInstaCropper.setImageUri(data?.data!!)
                Log.e("Gallery Image Uri", "data ${data?.data}")
                galleryImageUri = data?.data
                //imgPhoto.rotation = ImagesUtil.getImageOrientation(applicationContext, galleryImageUri).toFloat()
                imgPhoto.setImageURI(data?.data)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "WAAA", Toast.LENGTH_SHORT).show()
            }

            REQUEST_TO_CAMERA_GET_FULL_SIZE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                //data?.data!! is NULL when returning from any Camera Application
                setPic()

            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private fun showAppSettingsDialogFragment(message: String) {
        AppSettingsDialogFragment.newInstance(message).show(supportFragmentManager, "layout_camera_layout")
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

    private fun createPublicImageFile(): File {
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

    private fun setPic() {
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

    private fun intentToImageCapture() {
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
