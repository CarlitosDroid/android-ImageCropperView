package com.spidev.materialimagecropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.R.attr.bitmap
import android.content.res.Resources
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast


/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/14/17.
 */
open class MakeDrawableAsyncTask
(context: Context, uri: Uri, targetWidth: Int, targetHeight: Int)
    : AsyncTask<Void, Void, Drawable>() {
    //private var mContext: Context? = null

//
//    val mUri = uri
//
//
//    var rawWidth: Int = 0
//    var rawHeight: Int = 0
//
//

    private var mDrawable: Drawable? = null
    private var mUri: Uri? = null
    private var mTargetWidth: Int = 0
    private var mTargetHeight: Int = 0

    private val rawWidth: Int = 0
    private val rawHeight: Int = 0

    private var mContext: Context? = null

    init {
        mContext = context
        mUri = uri

        mTargetWidth = targetWidth
        mTargetHeight = targetHeight

        Log.e("INIT","INIT")

    }

    override fun doInBackground(vararg p0: Void?): Drawable {
        var options = BitmapFactory.Options()
        options.inSampleSize = 1
        options.inJustDecodeBounds = true


        BitmapFactory.decodeStream(mContext!!.contentResolver!!.openInputStream(mUri),
                null, options)

        val bitmapp = MediaStore.Images.Media.getBitmap(mContext?.contentResolver,
                mUri)

        Log.e("IN BACKGROUND","IN BACKGROIUND  $bitmapp")
        return BitmapDrawable(mContext?.resources, bitmapp)
    }

    override fun onPostExecute(result: Drawable?) {
        super.onPostExecute(result)
        mDrawable = result

        Log.e("OBJETTTTT2","OPBJETTTT "   + mDrawable)

    }


    fun getBitmap(context: Context, uri: Uri, options: BitmapFactory.Options) {


    }































}