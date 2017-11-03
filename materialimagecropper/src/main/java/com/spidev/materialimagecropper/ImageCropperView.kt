package com.spidev.materialimagecropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/13/17.
 */
class ImageCropperView : View {

    var gestureDetector: GestureDetector? = null
    var drawable: Drawable? = null
    var makeDrawableAsyncTask1: MakeDrawableAsyncTask1? = null

    private lateinit var mImageUri: Uri
    private var gridDrawable = GriddDrawable()
    private var mDrawable: Drawable? = null

    private val rectF = RectF()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs, defStyleAttr, 0)
    }

    fun initialize(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
//        gestureDetector = GestureDetector(this.context, object : GestureDetector.OnGestureListener {
//            override fun onShowPress(p0: MotionEvent?) {
//
//            }
//
//            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
//            }
//
//            override fun onDown(p0: MotionEvent?): Boolean {
//            }
//
//            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
//            }
//
//            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
//            }
//
//            override fun onLongPress(p0: MotionEvent?) {
//            }
//
//
//        })
    }

    fun setImageUri(uri: Uri) {
        mImageUri = uri

        invalidate()

    }

    fun startMakingSuitableDrawable() {
//        makeDrawableAsyncTask = MakeDrawableAsyncTask(context, mDrawable, mImageUri, width, width)
//
//        makeDrawableAsyncTask!!.execute()


        Log.e("INGRESAAA ", "INGRESAAA")

        //makeDrawableAsyncTask = MakeDrawableAsyncTask(context, mImageUri, width, height)

        makeDrawableAsyncTask1 = MakeDrawableAsyncTask1(mImageUri, width, height)

//        object : MakeDrawableAsyncTask(context, mImageUri, width, height) {
//
//            override fun onPostExecute(result: Drawable?) {
//                super.onPostExecute(result)
//
//                mDrawable = result
//                Log.e("INGRESAAA11 ","INGRESAAA11")
//                invalidate()
//
//
//                //mImageRawWidth = getRawWidth()
//                //mImageRawHeight = getRawHeight()
//
//                //onDrawableChanged()
//            }
//
//
//        }.execute()

        makeDrawableAsyncTask1?.execute()

    }


    fun crop(widthSpecification: Int, heightSpecification: Int) {

        if (mImageUri == null) {
            throw Throwable("Image uri is not set.")
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

//    inner class jaja : AsyncTask<Void, Void, Bitmap>() {
//        override fun doInBackground(vararg params: Void?): Bitmap {
//        }
//
//    }

    /**
     * (1)
     * This method is called before onLayout method
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("x-onMeasure", "onMeasure")


        //View Width and Height sizes, but in Pixel something like 640x480, 720x200
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Long number used for the setMeasuredDimension(,) for the ViewGroup
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)


        //values for
        var targetWidth = 1
        var targetHeight = 1

        when (widthMode) {

            MeasureSpec.EXACTLY -> {
                Log.e("x-WIDTH EXACTLY", "WIDTH EXACTLY")
                targetWidth = widthSize

                when (heightMode) {
                    MeasureSpec.EXACTLY -> {
                        Log.e("x-HEIGHT EXACTLY", "HEIGHT EXACTLY")
                        targetHeight = heightSize
                    }
                    MeasureSpec.AT_MOST -> {
                        Log.e("x-HEIGHT AT_MOST", "HEIGHT AT_MOST")
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        Log.e("x-HEIGHT UNSPECIFIED", "HEIGHT UNSPECIFIED")
                    }
                }


            }

            MeasureSpec.AT_MOST -> {
                Log.e("x-WIDTH AT_MOST", "WIDTH AT_MOST")
                when (heightMode) {
                    MeasureSpec.EXACTLY -> {
                        Log.e("x-HEIGHT EXACTLY", "HEIGHT EXACTLY")

                    }
                    MeasureSpec.AT_MOST -> {
                        Log.e("x-HEIGHT AT_MOST", "HEIGHT AT_MOST")
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        Log.e("x-HEIGHT UNSPECIFIED", "HEIGHT UNSPECIFIED")
                    }
                }
            }

            MeasureSpec.UNSPECIFIED -> {
                Toast.makeText(context, "UNSPECIFIED", Toast.LENGTH_SHORT).show()
            }

        }

        Log.e("x-targetWidth","targetWidth $targetWidth")
        Log.e("x-targetHeight","targetHeight $targetHeight")
        //esto es para los valores en el preview de android studio
        setMeasuredDimension(targetWidth, targetHeight)
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
        Log.e("x-mDrawable", "mDrawable $mDrawable")
        Log.e("x-RECF", "RECF ${rectF.left.toInt()}- ${rectF.top.toInt()}- ${rectF.right.toInt()}- ${rectF.bottom.toInt()}")

        // el parametro left
        // los dos primeros parametros parecen margin, los otros dos parametros anchura y altura
        //mDrawable?.setBounds(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())

        /**
         * Specify a bounding rectangle for the Drawable. This is where the drawable
         * will draw when its draw() method is called.
         * The first parameter (left) is a margin to left
         * The second parameter (top) is a margin to top
         * The third parameter (right) is the width of the rectangle
         * The fourth paramter (bottom) is the heigth of the rectangle
         */
        mDrawable?.setBounds(50, 50, 600, 500)
        mDrawable?.draw(canvas)
        //gridDrawable.draw(canvas)
    }


    inner class MakeDrawableAsyncTask1(var uri: Uri, targetWidth: Int, targetHeight: Int) : AsyncTask<Void, Void, Drawable>() {

        override fun doInBackground(vararg params: Void?): Drawable {
            var options = BitmapFactory.Options()
            options.inSampleSize = 1
            options.inJustDecodeBounds = true


            BitmapFactory.decodeStream(context.contentResolver!!.openInputStream(uri),
                    null, options)

            val bitmapp = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)

            Log.e("IN BACKGROUND", "IN BACKGROIUND  $bitmapp")
            return BitmapDrawable(context.resources, bitmapp)
        }

        override fun onPostExecute(result: Drawable?) {
            super.onPostExecute(result)
            mDrawable = result
            Log.e("INVALIDATEEE ", "INVALIDATEEEE")
            invalidate()
        }
    }
}


