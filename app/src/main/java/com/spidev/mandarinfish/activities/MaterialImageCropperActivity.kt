package com.spidev.mandarinfish.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.spidev.mandarinfish.R
import com.spidev.mandarinfish.util.ImagesUtil
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
        val destinationUri = intent.extras[MediaStore.EXTRA_OUTPUT]
//        Log.e("x-sourceUri ", "$sourceUri")
//        Log.e("x-destinationUri ", "$destinationUri")

        target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.e("onBitPrepareLoad", "onPrepareLoad $placeHolderDrawable")

                if (placeHolderDrawable != null) {

                }
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                Log.e("onBitmapFailed", "onBitmapFailed errorDrawable")
            }

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                Log.e("onBitmapLoaded", "onBitmapLoaded $bitmap")
                //ivFinal.setImageBitmap(bitmap)
                micPicture.setImageBitmap(bitmap)
            }
        }

        micPicture.tag = target

        Picasso.with(this)
                .load(sourceUri)
                .placeholder(R.drawable.ic_photo_blue_700_24dp)
                .into(target)

        fab.setOnClickListener { view ->
            Picasso.with(this)
                    .load(sourceUri)
                    .placeholder(R.drawable.ic_photo_blue_700_24dp)
                    .into(target)
        }

        fabShowExifData.setOnClickListener { _ ->
            ImagesUtil.showExifTag(this, sourceUri)
        }

        fabImageTest.setOnClickListener { _ ->
            micPicture.setImageBitmap(drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.kotlin2)))
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

}
