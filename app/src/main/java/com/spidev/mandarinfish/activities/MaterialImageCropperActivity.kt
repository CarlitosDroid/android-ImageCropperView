package com.spidev.mandarinfish.activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.spidev.mandarinfish.R
import com.spidev.mandarinfish.util.FileUtils
import com.spidev.mandarinfish.util.ImagesUtil
import com.spidev.materialimagecropper.CroppedBitmapCallback
import com.spidev.materialimagecropper.ImageCropperView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

import kotlinx.android.synthetic.main.activity_material_image_cropper.*
import kotlinx.android.synthetic.main.content_material_image_cropper.*

class MaterialImageCropperActivity : AppCompatActivity() {

    lateinit var target: Target

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_image_cropper)
        setSupportActionBar(toolbar)

        val sourceUri = intent.data

        target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.e("onPrepareLoad", "onPrepareLoad $placeHolderDrawable")

                if (placeHolderDrawable != null) {

                }
                val asdf = ImageCropperView(baseContext)
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                Log.e("onBitmapFailed", "onBitmapFailed errorDrawable")
            }

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                Log.e("onBitmapLoaded", "onBitmapLoaded")
                micPicture.setImageBitmap(bitmap)
            }
        }

        micPicture.tag = target

        fabCropImage.setOnClickListener { _ ->
            micPicture.cropImageAndResize(object : CroppedBitmapCallback {
                override fun onCroppedBitmapReady(croppedBitmap: Bitmap) {
                    val imagePath = FileUtils.saveToFile(croppedBitmap)
                    notifyMediaFileSystem(imagePath)
                }
            })
        }

        fabShowExifData.setOnClickListener { _ ->
            ImagesUtil.showExifTag(this, sourceUri)
        }

        fabRefresh.setOnClickListener { _ ->
            Picasso.with(this)
                    .load(sourceUri)
                    .placeholder(R.drawable.ic_photo_blue_700_24dp)
                    .into(target)
        }
    }

    private fun notifyMediaFileSystem(imagePath: String){
        MediaScannerConnection.scanFile(this@MaterialImageCropperActivity,
                arrayOf(imagePath), null) { path, uri ->
            Log.i("MediaScanner", "path $path")
            Log.i("MediaScanner", "uri $uri")
        }
    }
}
