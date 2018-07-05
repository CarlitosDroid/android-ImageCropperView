package com.spidev.mandarinfish.activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.spidev.mandarinfish.R
import com.spidev.mandarinfish.commons.Constants
import com.spidev.mandarinfish.util.ImagesUtil
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

        val destinationUri = intent.extras[MediaStore.EXTRA_OUTPUT]
        val preferredRatio = intent.extras[Constants.EXTRA_PREFERRED_RATIO]
        val minimunRatio = intent.extras[Constants.EXTRA_MINIMUN_RATIO]
        val maximunRatio = intent.extras[Constants.EXTRA_MAXIMUN_RATIO]
        val widthSpecification = intent.getIntExtra(Constants.EXTRA_WIDTH_SPECIFICATION,
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val heightSpecification = intent.getIntExtra(Constants.EXTRA_HEIGHT_SPECIFICATION,
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val quality = intent.extras[Constants.EXTRA_OUTPUT_QUALITY]

//        Log.e("x-sourceUri ", "$sourceUri")
//        Log.e("x-destinationUri ", "$destinationUri")
//        Log.e("x-preferredRatio ", "$preferredRatio")
//        Log.e("x-minimunRatio ", "$minimunRatio")
//        Log.e("x-maximunRatio ", "$maximunRatio")
//        Log.e("x-widthSpecification ", "$widthSpecification")
//        Log.e("x-heightSpecification ", "$heightSpecification")
//        Log.e("x-quality ", "$quality")


        //micPicture.crop(widthSpecification, heightSpecification)

        //micPicture.setImageUri()


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

        fabRefresh.setOnClickListener { _ ->
            Picasso.with(this)
                    .load(sourceUri)
                    .placeholder(R.drawable.ic_photo_blue_700_24dp)
                    .into(target)
        }

        fabShowExifData.setOnClickListener { _ ->
            ImagesUtil.showExifTag(this, sourceUri)
        }
    }
}
