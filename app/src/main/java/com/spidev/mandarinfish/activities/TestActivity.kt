package com.spidev.mandarinfish.activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.spidev.mandarinfish.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.content_test.*

class TestActivity : AppCompatActivity() {

    lateinit var target: Target

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        setSupportActionBar(toolbar)


        val sourceUri = intent.data
        fab.setOnClickListener { view ->


            Picasso.with(this)
                    .load(sourceUri)
                    .placeholder(R.drawable.ic_photo_blue_700_24dp)
                    .into(target)


        }

        target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.e("onPrepareLoad", "onPrepareLoad $placeHolderDrawable")

                ivFinal.setImageDrawable(placeHolderDrawable)
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                Log.e("onBitmapFailed", "onBitmapFailed errorDrawable")
            }

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                Log.e("onBitmapLoaded", "onBitmapLoaded")
                ivFinal.setImageBitmap(bitmap)
            }
        }

        ivFinal.tag = target


    }

}
