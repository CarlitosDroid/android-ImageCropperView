package com.spidev.materialimagecropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/13/17.
 */
class MaterialImageCropper : View {

    var gestureDetector: GestureDetector? = null
    var drawable: Drawable? = null
    var makeDrawableAsyncTask: MakeDrawableAsyncTask? = null

    private lateinit var mImageUri: Uri
    private var gridDrawable = GriddDrawable()
    private var mDrawable: Drawable? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs, defStyleAttr, 0)
    }

    fun initialize(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        gestureDetector = GestureDetector(this.context, object : GestureDetector.OnGestureListener {
            override fun onShowPress(p0: MotionEvent?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDown(p0: MotionEvent?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onLongPress(p0: MotionEvent?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        })
    }

    fun setImageUri(uri: Uri) {
        mImageUri = uri

        invalidate()

    }

    fun startMakingSuitableDrawable() {
        makeDrawableAsyncTask = MakeDrawableAsyncTask(context, mDrawable, mImageUri, width, width)

        makeDrawableAsyncTask!!.execute()

    }


    fun crop(widthSpecification: Int, heightSpecification: Int) {

        if (mImageUri == null) {
            throw IllegalStateException("Image uri is not set.")
        }

        val gridBounds = RectF(gridDrawable.bounds)

        val widthMode = MeasureSpec.getMode(widthSpecification)
        val widthSize = MeasureSpec.getSize(widthSpecification)
        val heightMode = MeasureSpec.getMode(heightSpecification)
        val heightSize = MeasureSpec.getSize(heightSpecification)

        when (widthMode) {
            MeasureSpec.EXACTLY -> {

            }
            MeasureSpec.AT_MOST -> {

            }

            MeasureSpec.UNSPECIFIED -> {

            }
        }

        requestLayout()
    }


    inner class jaja : AsyncTask<Void, Void, Bitmap>() {
        override fun doInBackground(vararg params: Void?): Bitmap {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    /**
     * (2)
     * This method is called before onDraw method
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.e("x-onLayout", "onLayout")

        startMakingSuitableDrawable()
    }

    /**
     * (3)
     * This method is called after onLayout method
     * First: we're going to convert our current bitmap to drawable if we want to add any paint over
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.e("x-onDraw", "onDraw")
        Log.e("OBJETTTTT1","OPBJETTTT "   + mDrawable)
        mDrawable?.setBounds(108, -228, 972, 1308)
        mDrawable?.draw(canvas)
        //gridDrawable.draw(canvas)
    }

}


