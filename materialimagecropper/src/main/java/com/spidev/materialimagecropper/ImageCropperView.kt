package com.spidev.materialimagecropper

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/13/17.
 */

class ImageCropperView : View {

    var drawable: Drawable? = null

    private var gridDrawable = GridDrawable()
    private var mDrawable: Drawable? = null

    private var mAnimator: ValueAnimator? = null

    /**
     * Dimensions of the view
     */
    private var viewWidth = 0f
    private var viewHeight = 0f

    /**
     * Dimensions of the drawable image
     */
    private var drawableImageWidth = 0f
    private var drawableImageHeight = 0f

    /**
     * This variable is used to scale the drawable
     */
    private var scale = 1f

    private val rectF = RectF()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs, defStyleAttr, 0)
    }

    fun initialize(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        mAnimator = ValueAnimator()
        mAnimator!!.duration = 400
        mAnimator!!.setFloatValues(0f, 1f)
        mAnimator!!.interpolator = DecelerateInterpolator(0.25f)
        mAnimator!!.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float

            val overScrollX = measureOverScrollX()
            val overScrollY = measureOverScrollY()
            Log.e("x-overScrollY", "overScrollY " + overScrollY)
            rectF.left -= (overScrollX * animatedValue)
            rectF.right = rectF.left + (drawableImageWidth * scale)

            rectF.top -= (overScrollY * animatedValue)
            rectF.bottom = rectF.top + (drawableImageHeight * scale)

            invalidate()
        }
    }

    fun setImageBitmap(bitmap: Bitmap) {
        drawableImageWidth = bitmap.width.toFloat()
        drawableImageHeight = bitmap.height.toFloat()
        mDrawable = BitmapDrawable(context.resources, bitmap)
        refreshDrawable()
    }

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

        //Calculating width and height of the view
        viewHeight = right.toFloat() - left.toFloat()
        viewWidth = bottom.toFloat() - top.toFloat()
    }

    /**
     * (3)
     * This method is called after onLayout method
     * First: we're going to convert our current bitmap to drawable if we want to add any paint over
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
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

        mDrawable?.setBounds(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        mDrawable?.draw(canvas)
        gridDrawable.draw(canvas)
    }

    private fun refreshDrawable() {
        setCoordinatesToRectangleAndGetTheDrawableScale()
        updateGridDrawable()
        invalidate()
    }

    private fun setCoordinatesToRectangleAndGetTheDrawableScale() {

        LogUtil.e("RAW IMAGE WIDTH ", "$drawableImageWidth")
        LogUtil.e("RAW IMAGE HEIGHT  ", "$drawableImageHeight")
        LogUtil.e("CURRENT RATIO ", "${getImageSizeRatio()}")
        LogUtil.e("ANCHO VISTA ", "$viewWidth")
        LogUtil.e("ALTO VISTA", "$viewHeight")

        if (getImageSizeRatio() >= 1f) { //The smallest side of the image is rawImageHeight
            Toast.makeText(context, "< 1 ", Toast.LENGTH_LONG).show()

            scale = getScale(viewHeight, drawableImageHeight)

            val newImageWidth = drawableImageWidth * scale

            val expansion = (newImageWidth - viewWidth) / 2

            rectF.set(-expansion, 0f, viewWidth + expansion, viewHeight)

        } else if (getImageSizeRatio() == 1f) { //The rawImageWidth and rawImageHeight are equals
            rectF.set(0f, 0f, viewWidth, viewHeight)
        } else {//The smallest side of the image is rawImageWidth
            Toast.makeText(context, ">= 1 ", Toast.LENGTH_LONG).show()

            scale = getScale(viewHeight, drawableImageWidth)

            val newImageHeight = drawableImageHeight * scale

            val expansion = (newImageHeight - viewHeight) / 2

            rectF.set(0f, -expansion, viewWidth, viewHeight + expansion)
        }
    }

    // 90 -> ancho < alto
    // 0 -> ancho > alto

    fun updateGridDrawable() {
        LogUtil.e("rectF left ", "${rectF.left}")
        LogUtil.e("rectF top ", "${rectF.top}")
        LogUtil.e("rectF right ", "${rectF.right}")
        LogUtil.e("rectF bottom ", "${rectF.bottom}")
        LogUtil.e("rectF width ", "${rectF.width()}")
        LogUtil.e("rectF height ", "${rectF.height()}")


        gridDrawable.setBounds(400, 10, 0, 20)
        /*if (getImageSizeRatio() == 1f) {

        } else if (getImageSizeRatio() < 1f) {
            gridDrawable.setBounds(rectF.left.toInt(), Math.abs(rectF.top.toInt()), rectF.right.toInt(), rectF.bottom.toInt())
        } else {
            gridDrawable.setBounds(Math.abs(rectF.left.toInt()), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        }*/
    }

    /**
     * Get the ratio of the image's size, the ratio is basically the relation
     * between the width and the height of the view, width:height -> k
     * for example: 4:3 -> 0.8, 16:9 -> 0.2, 1:1 -> 1
     */
    private fun getImageSizeRatio() = drawableImageWidth / drawableImageHeight

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

    companion object {
        const val DEFAULT_MINIMUM_RATIO = 4f / 5f
        const val DEFAULT_MAXIMUM_RATIO = 1.91f
        //RATIO VALUE BY DEFAULT TO SPECIFY A SQUARE(1:1)
        const val DEFAULT_RATIO = 1f
    }

    private var rawX = 0f
    private var rawY = 0f
    private var distanceX = 0f
    private var distanceY = 0f

    //TODO analyze this method
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                rawX = event.rawX
                rawY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                distanceX = event.rawX - rawX
                distanceY = event.rawY - rawY

                distanceX = applyOverScrollFix(distanceX, measureOverScrollX())
                distanceY = applyOverScrollFix(distanceY, measureOverScrollY())

                rectF.left += distanceX
                rectF.right += distanceX

                rectF.top += distanceY
                rectF.bottom += distanceY

                invalidate()

                rawX = event.rawX
                rawY = event.rawY
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                mAnimator!!.start()
            }
        }
        return true
    }

    private fun applyOverScrollFix(dx: Float, overScroll: Float): Float {
        var dx1 = dx
        //overscroll -> 0 a 1080

        //offRatio crece de 0 a +
        // a lo maximo llega a 2.5 porque tu dedo sale de la pantalla
        val offRatio = Math.abs(overScroll) / viewWidth

        dx1 -= dx1 * Math.sqrt(offRatio.toDouble()).toFloat()

        return dx1
    }

    /**
     * This method evaluate internal difference between view and drawable image,
     * and these difference are visible for the user.
     * for example: if the user scroll the drawable image more to the right than the view left side,
     * the user will see a part of the background in the view left side.
     * This method measure is called when the use scroll the drawable image and
     * when the drawable image return to it's position through animator
     */
    private fun measureOverScrollX(): Float {

        // Is drawable width smaller than view width
        // Then we have to return the 'distance between the center points of the x-axis only'
        if (rectF.width() <= viewWidth) {
            return rectF.centerX() - viewWidth / 2
        }

        // Is drawable width bigger than view width
        // Then we don't have any internal difference of the x-axis that's why we return 0
        if (rectF.left <= 0 && rectF.right >= viewWidth) {
            return 0f
        }

        // Is drawable left side more to the right than left side of the view
        // Then we have internal difference between view left side and drawable left side
        // and we returned that difference
        if(rectF.left > 0){
            return rectF.left
        }

        // Is drawable right side more to the left than right side of the view
        // Then we have internal difference between view right side and drawable right side
        // and we returned that difference
        if(rectF.right < viewWidth){
            return rectF.right - viewWidth
        }

        return 0f
    }

    /**
     * This method evaluate internal difference between view and drawable image,
     * and these difference are visible for the user.
     * for example: if the user scroll the drawable image more to the bottom than the view top side,
     * the user will see a part of the background in the view top side
     * This method measure is called when the use scroll the drawable image and
     * when the drawable image return to it's position through animator
     */
    private fun measureOverScrollY(): Float {

        // Is drawable height smaller than view height
        // Then we have to return the 'distance between the center points of the y-axis only'
        if (rectF.height() <= viewHeight) {
            return rectF.centerY() - viewHeight / 2
        }

        // Is drawable height bigger than view height
        // Then we don't have any internal difference of the y-axis that's why we return 0
        if (rectF.top <= 0 && rectF.bottom >= viewHeight) {
            return 0f
        }

        // Is drawable top side more to the bottom than top side of the view
        // Then we have internal difference between view top side and drawable top side
        // and we returned that difference
        if(rectF.top > 0){
            return rectF.top
        }

        // Is drawable bottom side more to the top than bottom side of the view
        // Then we have internal difference between view bottom side and drawable bottom side
        // and we returned that difference
        if(rectF.bottom < viewHeight){
            return rectF.bottom - viewHeight
        }

        return 0f
    }

}


