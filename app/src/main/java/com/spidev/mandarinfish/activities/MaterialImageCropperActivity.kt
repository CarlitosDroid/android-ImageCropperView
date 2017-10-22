package com.spidev.mandarinfish.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.spidev.mandarinfish.R
import com.spidev.mandarinfish.commons.Constants

import kotlinx.android.synthetic.main.activity_material_image_cropper.*
import kotlinx.android.synthetic.main.content_material_image_cropper.*

class MaterialImageCropperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_image_cropper)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val sourceUri = intent.data

        val destinationUri = intent.extras[MediaStore.EXTRA_OUTPUT]
        val preferredRatio = intent.extras[Constants.EXTRA_PREFERRED_RATIO]
        val minimunRatio = intent.extras[Constants.EXTRA_MINIMUN_RATIO]
        val maximunRatio = intent.extras[Constants.EXTRA_MAXIMUN_RATIO]
        val widthSpecification = intent.extras[Constants.EXTRA_WIDTH_SPECIFICATION]
        val heightSpecification = intent.extras[Constants.EXTRA_HEIGHT_SPECIFICATION]
        val quality = intent.extras[Constants.EXTRA_OUTPUT_QUALITY]

        Log.e("x-sourceUri ", "$sourceUri")
        Log.e("x-destinationUri ", "$destinationUri")
        Log.e("x-preferredRatio ", "$preferredRatio")
        Log.e("x-minimunRatio ", "$minimunRatio")
        Log.e("x-maximunRatio ", "$maximunRatio")
        Log.e("x-widthSpecification ", "$widthSpecification")
        Log.e("x-heightSpecification ", "$heightSpecification")
        Log.e("x-quality ", "$quality")


        //micPicture.setImageUri()
    }


}