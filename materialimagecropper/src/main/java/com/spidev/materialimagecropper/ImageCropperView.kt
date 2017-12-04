package com.spidev.materialimagecropper

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.support.media.ExifInterface.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.View
import android.widget.Toast

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/13/17.
 */

const val DEFAULT_MINIMUM_RATIO = 4f / 5f
const val DEFAULT_MAXIMUM_RATIO = 1.91f
//RATIO VALUE BY DEFAULT TO SPECIFY A SQUARE(1:1)
const val DEFAULT_RATIO = 1f

class ImageCropperView : View {

    var gestureDetector: GestureDetector? = null
    var drawable: Drawable? = null
    var makeDrawableAsyncTask: MakeDrawableAsyncTask? = null

    private lateinit var mImageUri: Uri
    private var gridDrawable = GridDrawable()
    private var mDrawable: Drawable? = null

    /**
     * I dont know
     */
    private var mWidth = 0f
    private var mHeight = 0f

    /**
     * I dont know
     */
    private var rawImageWidth = 0f
    private var rawImageHeight = 0f

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

    fun setImageUri() {
        mImageUri = Uri.parse("content://media/external/images/media/22763")

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
     * @param widthMeasureSpec horizontal space requirement imposed by the PARENT
     * @param heightMeasureSpec vertical space requirement imposed by the PARENT
     * widthMeasureSpec and heightMeasureSpec are values imposed by the PARENT
     * for example:
     * XXHDPI -> 200dp x 200dp -> widthMeasureSpec 600px, heightMeasureSpec 600px
     *
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("x-onMeasure", "onMeasure")


        //View Width and Height sizes of the PARENT, but in Pixel something like 640x480, 720x200
        val parentWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeightSize = MeasureSpec.getSize(heightMeasureSpec)

        Log.e("x-parent-w-h-size", " $parentWidthSize x $parentHeightSize")

        //Long number used for the setMeasuredDimension(,) for the ImageCropperView
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        //values for
        var targetWidth = 1
        var targetHeight = 1

        when (widthMode) {

            MeasureSpec.EXACTLY -> {
                Log.e("x-WIDTH EXACTLY", "WIDTH EXACTLY")
                targetWidth = parentWidthSize

                when (heightMode) {
                    MeasureSpec.EXACTLY -> {
                        Log.e("x-HEIGHT EXACTLY", "HEIGHT EXACTLY")
                        targetHeight = parentHeightSize
                    }
                    MeasureSpec.AT_MOST -> {
                        Log.e("x-HEIGHT AT_MOST", "HEIGHT AT_MOST")
                        targetWidth = parentWidthSize
                        targetHeight = parentWidthSize
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
                        // if we have a vertical line, wrap_content-match_parent
                        // set the all the height of the parent
                        // and the minium between the width of the parent or the height of the parent x ratio
                        targetHeight = parentWidthSize
                        targetWidth = parentWidthSize

                    }
                    MeasureSpec.AT_MOST -> {
                        Log.e("x-HEIGHT AT_MOST", "HEIGHT AT_MOST")

                        var specRatio = parentWidthSize.toFloat() / parentHeightSize.toFloat()
                        Log.e("x-DEFAULT_RATIO", "DEFAULT_RATIO $DEFAULT_RATIO")
                        Log.e("x-DEFAULT_RATIO", "DEFAULT_RATIO $specRatio")

                        if (specRatio == DEFAULT_RATIO) {
                            targetWidth = parentWidthSize
                            targetHeight = parentHeightSize
                        } else if (specRatio > DEFAULT_RATIO) {
                            targetWidth = (targetHeight * DEFAULT_RATIO).toInt()
                            targetHeight = parentHeightSize
                        } else {
                            targetWidth = parentWidthSize
                            targetHeight = (targetWidth / DEFAULT_RATIO).toInt()
                        }

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
        mHeight = right.toFloat() - left.toFloat()
        mWidth = bottom.toFloat() - top.toFloat()

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
        makeDrawableAsyncTask = MakeDrawableAsyncTask(mImageUri)
        makeDrawableAsyncTask?.execute()
    }

    /**
     * This AsyncTask class transform a image's uri to a rotated bitmap drawable
     * @param imageUri The URI of the image
     * @return A bitmap drawable object, basically a rotated drawable
     */
    inner class MakeDrawableAsyncTask(var imageUri: Uri) : AsyncTask<Void, Void, Drawable>() {

        override fun doInBackground(vararg params: Void?): Drawable {
            var options = BitmapFactory.Options()
            options.inSampleSize = 1
            options.inJustDecodeBounds = true

            BitmapFactory.decodeStream(context.contentResolver!!.openInputStream(imageUri),
                    null, options)

            when (ImagesUtil.getImageOrientation(context, mImageUri)) {
                ORIENTATION_NORMAL, ORIENTATION_ROTATE_180 -> {
                    rawImageWidth = options.outWidth.toFloat()
                    rawImageHeight = options.outHeight.toFloat()
                }
                ORIENTATION_ROTATE_90, ORIENTATION_ROTATE_270 -> {
                    rawImageWidth = options.outHeight.toFloat()
                    rawImageHeight = options.outWidth.toFloat()
                }
                else -> {
                    rawImageWidth = options.outWidth.toFloat()
                    rawImageHeight = options.outHeight.toFloat()
                }
            }

            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)

            val matrix = Matrix()
            matrix.postRotate(ImagesUtil.getImageRotation(context, mImageUri).toFloat())

            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

            return BitmapDrawable(context.resources, rotatedBitmap)
        }

        override fun onPostExecute(result: Drawable?) {
            super.onPostExecute(result)
            mDrawable = result

            refreshDrawable()
        }
    }

    private fun refreshDrawable() {
        setCoordinatesToRectangleAndGetTheDrawableScale()
        //updateGridDrawable()
        invalidate()
    }

    private fun setCoordinatesToRectangleAndGetTheDrawableScale() {

        LogUtil.e("RAW IMAGE WIDTH ", "$rawImageWidth")
        LogUtil.e("RAW IMAGE HEIGHT  ", "$rawImageHeight")
        LogUtil.e("CURRENT RATIO ", "${getImageSizeRatio()}")
        LogUtil.e("ANCHO VISTA ", "${mWidth}")
        LogUtil.e("ALTO VISTA", "$mHeight")

        Toast.makeText(context, "ORIENTATION " + ImagesUtil.getImageOrientation(context, mImageUri), Toast.LENGTH_LONG).show()

        //ONLY IN PORTRAIT- PORQUE SI LO PONEMOS LANDSCAPE TENDRAS UN CUADRADO QUE NO SE VERA EN LA PANTALLA
        var scale = 1f
        if (getImageSizeRatio() >= 1f) { //The smallest side of the image is rawImageWidth
            Toast.makeText(context, ">= 1 ", Toast.LENGTH_LONG).show()

            scale = getScale(mHeight, rawImageWidth)

            val newImageHeight = rawImageHeight * scale

            val expansion = (newImageHeight - mHeight) / 2

            rectF.set(0f, -expansion, mWidth, mHeight + expansion)

        } else if (getImageSizeRatio() == 1f) { //The rawImageWidth and rawImageHeight are equals
            rectF.set(0f, 0f, mWidth, mHeight)
        } else {//The smallest side of the image is rawImageHeight
            Toast.makeText(context, "< 1 ", Toast.LENGTH_LONG).show()

            scale = getScale(mHeight, rawImageHeight)

            val newImageWidth = rawImageWidth * scale

            val expansion = (newImageWidth - mWidth) / 2

            rectF.set(-expansion, 0f, mWidth + expansion, mHeight)
        }
    }

    fun updateGridDrawable() {
        LogUtil.e("rectF left ", "${rectF.left}")
        LogUtil.e("rectF top ", "${rectF.top}")
        LogUtil.e("rectF right ", "${rectF.right}")
        LogUtil.e("rectF bottom ", "${rectF.bottom}")
        LogUtil.e("rectF width ", "${rectF.width()}")
        LogUtil.e("rectF height ", "${rectF.height()}")

        if (getImageSizeRatio() == 1f) {

        } else if (getImageSizeRatio() < 1f) {
            gridDrawable.setBounds(rectF.left.toInt(), Math.abs(rectF.top.toInt()), rectF.right.toInt(), rectF.bottom.toInt())
        } else {
            gridDrawable.setBounds(Math.abs(rectF.left.toInt()), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        }
    }

    /**
     * Get the ratio of the image's size, the ratio is basically the relation
     * between the width and the height of the view, width:height -> k
     * for example: 4:3 -> 0.8, 16:9 -> 0.2, 1:1 -> 1
     */
    private fun getImageSizeRatio() = rawImageHeight / rawImageWidth


    private fun getScale(smallestSideOfView: Float, smallestSideOfImage: Float) = smallestSideOfView / smallestSideOfImage

    /**
     * @return 0, 90, 180 or 270. 0 could be returned if there is no data about rotation
     */
    fun getImageOrientation(context: Context, imageUri: Uri): Int {
        return getRotationFromMediaStore(context, imageUri)
    }

    //TODO validar quizas solo es para android nougat
    fun getRotationFromMediaStore(context: Context, imageUri: Uri): Int {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION)
        val cursor = context.contentResolver.query(imageUri, columns, null, null, null) ?: return 0

        cursor.moveToFirst()

        val orientationColumnIndex = cursor.getColumnIndex(columns[1])
        return cursor.getInt(orientationColumnIndex)
    }

}


