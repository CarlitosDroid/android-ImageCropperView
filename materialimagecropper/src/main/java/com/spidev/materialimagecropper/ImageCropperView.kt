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

const val DEFAULT_MINIMUM_RATIO = 4f / 5f
const val DEFAULT_MAXIMUM_RATIO = 1.91f
const val DEFAULT_RATIO = 1f

class ImageCropperView : View {

    var gestureDetector: GestureDetector? = null
    var drawable: Drawable? = null
    var makeDrawableAsyncTask: MakeDrawableAsyncTask? = null

    private lateinit var mImageUri: Uri
    private var gridDrawable = GriddDrawable()
    private var mDrawable: Drawable? = null


    /**
     * I dont know
     */
    private var mWidth = 0f
    private var mHeight = 0f

    /**
     * I dont know
     */
    private var rawWidth = 0
    private var rawHeight = 0

    /**
     * Setting up ratios default values
     */
    private var minimumRatio = DEFAULT_MINIMUM_RATIO
    private var maximumRatio = DEFAULT_MAXIMUM_RATIO

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

    /**
     * is it neccesary?
     */
    private fun setRatio(minimumRatio: Float, maximumRatio: Float) {
        //minimumRatio = this@ImageCropperView.minimumRatio
        //maximumRatio = maximumRatio
    }


    fun setImageUri(uri: Uri) {
        mImageUri = uri

        invalidate()

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

        Log.e("x-targetWidth", "targetWidth $targetWidth")
        Log.e("x-targetHeight", "targetHeight $targetHeight")
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

        LogUtil.e("onLayout-left", left.toString())
        LogUtil.e("onLayout-rigth", right.toString())
        LogUtil.e("onLayout-bottom", bottom.toString())
        LogUtil.e("onLayout-top", top.toString())

        //Calculating width and height of the view
        mWidth = right.toFloat() - left.toFloat()
        mHeight = bottom.toFloat() - top.toFloat()

        LogUtil.e("onLayout-mWidth", mWidth.toString())
        LogUtil.e("onLayout-mHeight", mHeight.toString())
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
        //mDrawable?.setBounds(50, 50, 600, 500)
        /**
         * VER LA DIFERENCIA ENTRE RECTF Y RECT - ---> porque setBounds tambien recive un rectangulo
         */

        //mDrawable.bounds = rectF
        mDrawable?.setBounds(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        mDrawable?.draw(canvas)
        //gridDrawable.draw(canvas)
    }

    /**
     * Setea los dos vertices del rectangulo
     */

    fun startMakingSuitableDrawable() {
        makeDrawableAsyncTask = MakeDrawableAsyncTask(mImageUri, mWidth.toInt(), mHeight.toInt())
        makeDrawableAsyncTask?.execute()
    }

    inner class MakeDrawableAsyncTask(var uri: Uri, mWidth: Int, mHeight: Int) : AsyncTask<Void, Void, Drawable>() {

        override fun doInBackground(vararg params: Void?): Drawable {
            var options = BitmapFactory.Options()
            options.inSampleSize = 1
            options.inJustDecodeBounds = true

            BitmapFactory.decodeStream(context.contentResolver!!.openInputStream(uri),
                    null, options)

            rawWidth = options.outWidth
            rawHeight = options.outHeight

            val bitmapp = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)

            Log.e("IN BACKGROUND", "IN BACKGROIUND  $bitmapp")
            return BitmapDrawable(context.resources, bitmapp)
        }

        override fun onPostExecute(result: Drawable?) {
            super.onPostExecute(result)
            mDrawable = result

            refreshDrawable()
        }
    }

    private fun refreshDrawable() {
        setCoordinatesToRectangleAndGetTheDrawableScale()
        invalidate()
    }


    private fun setCoordinatesToRectangleAndGetTheDrawableScale() {


        if (false) {
            // TODO ESTA PARA ESTA PARA ANALIZAR
        } else if (rawWidth < mWidth || rawHeight < mHeight) {
        // TODO AQUI SI USAMOS EL rawWidth y rawHeight en el rectangulo
        } else {
            // Si la relacion o ratio entre el ancho y alto, es mas pequeño que el minimo 0.8f
            // significa que tenemos una vista con un ancho mucho mas pequeño que el alto
            //      =====
            //      =   =               =============
            //      =   =               =           =
            //  if  =   =      else     =           =
            //      =   =               =============
            //      =====
            LogUtil.e("CURRENT RATIO ", "${getImageSizeRatio()}")
            if (getImageSizeRatio() < minimumRatio) {
                LogUtil.e("MENOR RATIO ", "$minimumRatio")
                LogUtil.e("ANCHO ", "${mHeight * minimumRatio}")
                LogUtil.e("ALTO ", "$mHeight")
                rectF.set(0f, 0f, mHeight * minimumRatio, mHeight)
            } else {
                LogUtil.e("MAYOR RATIO ", "$maximumRatio")
                LogUtil.e("ANCHO ", "$mWidth")
                LogUtil.e("ALTO ", "${mWidth / maximumRatio}")
                rectF.set(0f, 0f, mWidth, mWidth / maximumRatio)
            }
        }



    }

    /**
     * Get the ratio of the image's size, the ratio is basically the relation
     * between the width and the height of the view, width:height -> k
     * for example: 4:5 -> 0.8, 2:10 -> 0.2, 1:1 -> 1
     */
    private fun getImageSizeRatio() = rawWidth / rawHeight

}

