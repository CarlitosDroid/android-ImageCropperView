package com.spidev.materialimagecropper

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/14/17.
 */
class MakeDrawableAsyncTask constructor(context: Context, uri: Uri, targetWidth: Int, targetHeight: Int) : AsyncTask<Void, Void, Drawable>() {
    override fun doInBackground(vararg p0: Void?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}