package com.spidev.materialimagecropper

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/13/17.
 */

class ImageCropperView : View {

    /**
     * For handling movement on drawable image
     */
    private lateinit var gestureDetector: GestureDetector

    /**
     * For handling the zoom in and zoom out on the drawable image
     */
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var gridDrawable = GridDrawable()

    /**
     * Basically our drawable image
     */
    private var bitmapDrawable: BitmapDrawable? = null

    /**
     * The animator for moving the drawable image to its initial position
     * when the user ACTION UP
     */
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
    private var drawableImageScale = 1f

    /**
     * The rectangle for handling the bounds of the drawable image
     */
    private val rectF = RectF()

    /**
     * The displacement of the image
     * we only need the left and top because the others can be calculated from these
     */
    private var mDisplayDrawableLeft = 0f
    private var mDisplayDrawableTop = 0f

    private var scaledDrawableImageWidth = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs, defStyleAttr, 0)
    }

    private fun initialize(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        mAnimator = ValueAnimator()
        mAnimator!!.duration = 400
        mAnimator!!.setFloatValues(0f, 1f)
        mAnimator!!.interpolator = DecelerateInterpolator(0.25f)
        mAnimator!!.addUpdateListener(onSettleAnimatorUpdateListener)
        gestureDetector = GestureDetector(context, onGestureListener)
        scaleGestureDetector = ScaleGestureDetector(context, onScaleGestureListener)
    }

    /**
     * This function is executed by the user at each moment
     * @param bitmap The bitmap to be showed
     */
    fun setImageBitmap(bitmap: Bitmap) {
        drawableImageWidth = bitmap.width.toFloat()
        drawableImageHeight = bitmap.height.toFloat()
        bitmapDrawable = BitmapDrawable(context.resources, bitmap)
        refreshDrawable()
    }

    private fun refreshDrawable() {
        placeScaledDrawableImageInTheCenter()
        updateGridDrawable()
        invalidate()
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

        rectF.left = mDisplayDrawableLeft
        rectF.top = mDisplayDrawableTop
        rectF.right = rectF.left + getScaledDrawableImageWidth(drawableImageWidth, drawableImageScale)
        rectF.bottom = rectF.top + getScaledDrawableImageHeight(drawableImageHeight, drawableImageScale)

        bitmapDrawable?.setBounds(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        bitmapDrawable?.draw(canvas)
        gridDrawable.draw(canvas)
    }

    /**
     * This method place the drawable image inside the view
     */
    private fun placeDrawableImageInTheCenter() {
       //TODO probably, here we can make the opposite functionality
    }

    /**
     * This method adjusts the image to the view, and the placed it center
     */
    private fun placeScaledDrawableImageInTheCenter() {
        when {
        //The smallest side of the image is rawImageHeight
            getImageSizeRatio() >= 1f -> {
                drawableImageScale = getScale(viewHeight, drawableImageHeight)
                scaledDrawableImageWidth = getScaledDrawableImageWidth(drawableImageWidth, drawableImageScale)
                val expansion = (scaledDrawableImageWidth - viewWidth) / 2
                mDisplayDrawableLeft = -expansion
                mDisplayDrawableTop = 0f
            }
        //The rawImageWidth and rawImageHeight are equals
            getImageSizeRatio() == 1f -> {
                mDisplayDrawableLeft = 0f
                mDisplayDrawableTop = 0f
            }
        //The smallest side of the image is rawImageWidth
            else -> {
                drawableImageScale = getScale(viewHeight, drawableImageWidth)
                val newImageHeight = getScaledDrawableImageHeight(drawableImageHeight, drawableImageScale)
                val expansion = (newImageHeight - viewHeight) / 2
                mDisplayDrawableLeft = 0f
                mDisplayDrawableTop = -expansion
            }
        }
    }

    /**
     * This method scales the drawable image width
     */
    private fun getScaledDrawableImageWidth(drawableImageWidth: Float, drawableImageScale: Float) =
            drawableImageWidth * drawableImageScale

    /**
     * This method scales the drawable image height
     */
    private fun getScaledDrawableImageHeight(drawableImageHeight: Float, drawableImageScale: Float) =
            drawableImageHeight * drawableImageScale

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

    companion object {
        const val DEFAULT_MINIMUM_RATIO = 4f / 5f
        const val DEFAULT_MAXIMUM_RATIO = 1.91f
        //RATIO VALUE BY DEFAULT TO SPECIFY A SQUARE(1:1)
        const val DEFAULT_RATIO = 1f
    }

    /**
     * We override onTouchEvent for handling movements action on drawable image
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {

        gestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                Log.e("ACTION UP", "ACTION UP")
                mAnimator!!.start()
            }
        }
        return true
    }

    /**
     * These function stops the movement little by little when the user scroll the drawable image
     * @param dAtEachNewPoint The displacement at each new point as the user moves the drawable image
     * on the x-axis or y-axis.
     * @param overScroll The displacement of the drawable image, negative or positive
     * @return The new displacement at each new point
     */
    private fun applyOverScrollFix(dAtEachNewPoint: Float, overScroll: Float): Float {
        var newDAtEachNewPoint = dAtEachNewPoint
        // We divide the absolute value of overScroll between view width
        // for example: When drawable image has the same width like the view
        // overScroll -> 0 to 540
        // viewWidth -> 1080
        // offRatio -> 0.0 to 0.5
        val offRatio = Math.abs(overScroll) / viewWidth
        //basically we subtract the square root at each new point to reduce the movement
        newDAtEachNewPoint -= newDAtEachNewPoint * Math.sqrt(offRatio.toDouble()).toFloat()
        return newDAtEachNewPoint
    }

    /**
     * This method is called when the use scroll the drawable image and when the drawable image
     * return to it's position through animator.
     * This method evaluate four mains scenarios for example:
     * (1) If drawable image width is equals or smaller than view width
     * (2) If drawable image width is bigger than view width
     * (3) If drawable image left side is more to the right than left side of the view
     * (4) If drawable image right side is more to the left than right side of the view
     */
    private fun measureOverScrollX(): Float {

        // If drawable image width is equals or smaller than view width
        // Then we have to return the 'distance between the CENTER POINTS of the drawable image and the view'
        // only in the X-axis
        if (rectF.width() <= viewWidth) {
            return rectF.centerX() - viewWidth / 2
        }

        // If drawable image width is bigger than view width
        // Then we don't have any internal difference of the x-axis that's why we return 0
        if (rectF.left <= 0 && rectF.right >= viewWidth) {
            return 0f
        }

        // If drawable image left side is more to the right than left side of the view
        // Then we have internal difference between view left side and drawable left side
        // and we returned that difference
        if (rectF.left > 0) {
            return rectF.left
        }

        // If drawable image right side is more to the left than right side of the view
        // Then we have internal difference between view right side and drawable right side
        // and we returned that difference
        if (rectF.right < viewWidth) {
            return rectF.right - viewWidth
        }

        return 0f
    }

    /**
     * This method is called when the use scroll the drawable image and when the drawable image
     * return to it's position through animator.
     * This method evaluate four mains scenarios for example:
     * (1) If drawable image height is equals or smaller than view height
     * (2) If drawable image height is bigger than view height
     * (3) If drawable image top side is more to the bottom than top side of the view
     * (4) If drawable image bottom side is more to the top than bottom side of the view
     */
    private fun measureOverScrollY(): Float {

        // If drawable image height is equals or smaller than view height
        // Then we have to return the 'distance between the CENTER POINTS of the drawable image and the view'
        // only in the Y-axis
        if (rectF.height() <= viewHeight) {
            return rectF.centerY() - viewHeight / 2
        }

        // If drawable image height is bigger than view height
        // Then we don't have any internal difference of the y-axis that's why we return 0
        if (rectF.top <= 0 && rectF.bottom >= viewHeight) {
            return 0f
        }

        // If drawable image top side is more to the bottom than top side of the view
        // Then we have internal difference between view top side and drawable top side
        // and we returned that difference
        if (rectF.top > 0) {
            return rectF.top
        }

        // If drawable image bottom side is more to the top than bottom side of the view
        // Then we have internal difference between view bottom side and drawable bottom side
        // and we returned that difference
        if (rectF.bottom < viewHeight) {
            return rectF.bottom - viewHeight
        }

        return 0f
    }

    /**
     * Listener for handling movement of the user on the drawable image
     */
    private var onGestureListener = object : GestureDetector.OnGestureListener {

        override fun onShowPress(e: MotionEvent?) {
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            var mDistanceX = -distanceX
            var mDistanceY = -distanceY

            mDistanceX = applyOverScrollFix(mDistanceX, measureOverScrollX())
            mDistanceY = applyOverScrollFix(mDistanceY, measureOverScrollY())

            mDisplayDrawableLeft += mDistanceX
            mDisplayDrawableTop += mDistanceY

            invalidate()
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
        }
    }

    /**
     * This listener reacts when the user touch the drawable image with 2 or more fingers
     * with this listener we can handle the zoom in and zoom out of the image
     */
    private var onScaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            //TODO ANALYSING THIS METHOD
            //imageScale 1.5
            //overScale 1.8

            //tiendre a crecer
            // overScale = drawableImageScale / 0.8

            val overScale = measureOverScale()

            //cuando expando es 1.000
            //cuando collapso es 0.90000
            //Log.e("FACTOR" , "FACTOR " + mScaleFactor)
            //Log.e("FOCUSX" , "FOCUSX " + detector.focusX)
            //Log.e("FOCUSY" , "FOCUSY " + detector.focusY)

            val scale = applyOverScaleFix(detector.scaleFactor, overScale)

            drawableImageScale *= scale

            //rectF.left = mDisplayDrawableLeft
            //rectF.top = mDisplayDrawableTop

            //Log.e("LEFFFT ","LEFFTT " + rectF.left)
            Log.e("TOPP ", "TOPP " + rectF.top)
            //Log.e("RIGHT ","RIGHT " + bounds.right);
            //Log.e("BOTTOM ","BOTTOM " + mDisplayDrawableLeft);

            invalidate()
            return true
        }
    }

    private val MAXIMUM_OVER_SCALE = 0.7f

    //TODO ANALYZING THIS CODE
    /**
     * Returning the scale
     */
    private fun applyOverScaleFix(scaleFactor: Float, overScale: Float): Float {
        var mOverScale = overScale
        var mScaleFactor = scaleFactor

        //si no hay over scale
        if (mOverScale == 1f) {
            return mScaleFactor
        }

        //
        if (mOverScale > 1) {
            mOverScale = 1f / mOverScale
        }

        val wentOverScaleRatio = (mOverScale - MAXIMUM_OVER_SCALE) / (1 - MAXIMUM_OVER_SCALE)

        mScaleFactor *= wentOverScaleRatio + (1 - wentOverScaleRatio) / scaleFactor

        return scaleFactor
    }

    /**
     * Nuestra imagen puede sobreescalarse un valor entre 0.83f
     * for instance:
     * esto hace que si nuestra scala inicial es 1.5 aumente hasta 1.8
     * 1.5 / 0.83 = 1.8
     */
    private fun measureOverScale(): Float {
        if (drawableImageScale < 0.83f) {
            return drawableImageScale / 0.83f
        }

        if (drawableImageScale > 0.83f) {
            return drawableImageScale / 0.83f
        }
        return 1f
    }


    /**
     * Listener for settling drawable image after user ACTION UP
     */
    private var onSettleAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val animatedValue = animation.animatedValue as Float

        val overScrollX = measureOverScrollX()
        val overScrollY = measureOverScrollY()

        // for example image that user move the image (100, 0) and then ACTION UP
        // (overScrollX * animatedValue) increase its value from 0 to 50 and then 50 to 0
        // between the duration time setted only for showing an velocity effect
        mDisplayDrawableLeft -= overScrollX * animatedValue
        mDisplayDrawableTop -= overScrollY * animatedValue

        invalidate()
    }
}


