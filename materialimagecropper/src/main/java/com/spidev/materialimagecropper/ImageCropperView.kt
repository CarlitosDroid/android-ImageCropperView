package com.spidev.materialimagecropper

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 8/13/17.
 */

class ImageCropperView : View {

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
     * These variables save the initial position and the last position of the drawable image
     */
    private var rawX = 0f
    private var rawY = 0f

    /**
     * dxAtEachNewPoint is our displacement X-axis at each new point
     * dyAtEachNewPoint is our displacement Y-axis at each new point
     * these values can be negative or positive depends on direction
     * for instance:
     * (20, 10) initial position(ACTION_DOWN) -> (25, 5) final position(ACTION_MOVE)
     * dxAtEachNewPoint is 25 - 20 = 5, dyAtEachNewPoint is 5 - 10 = -5
     * (25, 5) (last position) -> (31, 0) (final position)
     * dxAtEachNewPoint = 6, dyAtEachNewPoint = -5
     */
    private var dxAtEachNewPoint = 0f
    private var dyAtEachNewPoint = 0f

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

            // for example image that user move the image (100, 0) and then ACTION UP
            // (overScrollX * animatedValue) increase its value from 0 to 50 and then 50 to 0
            // between the duration time setted only for showing an velocity effect
            rectF.left -= (overScrollX * animatedValue)
            rectF.right = rectF.left + (drawableImageWidth * drawableImageScale)

            rectF.top -= (overScrollY * animatedValue)
            rectF.bottom = rectF.top + (drawableImageHeight * drawableImageScale)

            invalidate()
        }

        scaleGestureDetector = ScaleGestureDetector(context, onScaleGestureListener)
    }

    fun setImageBitmap(bitmap: Bitmap) {
        drawableImageWidth = bitmap.width.toFloat()
        drawableImageHeight = bitmap.height.toFloat()
        bitmapDrawable = BitmapDrawable(context.resources, bitmap)
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

        bitmapDrawable?.setBounds(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        bitmapDrawable?.draw(canvas)
        gridDrawable.draw(canvas)
    }

    private fun refreshDrawable() {
        setCoordinatesToRectangleAndGetTheDrawableScale()
        updateGridDrawable()
        invalidate()
    }

    /**
     * This method set the initial scale of the drawable image
     */
    private fun setCoordinatesToRectangleAndGetTheDrawableScale() {

        LogUtil.e("RAW IMAGE WIDTH ", "$drawableImageWidth")
        LogUtil.e("RAW IMAGE HEIGHT  ", "$drawableImageHeight")
        LogUtil.e("CURRENT RATIO ", "${getImageSizeRatio()}")
        LogUtil.e("ANCHO VISTA ", "$viewWidth")
        LogUtil.e("ALTO VISTA", "$viewHeight")

        if (getImageSizeRatio() >= 1f) { //The smallest side of the image is rawImageHeight
            Toast.makeText(context, "< 1 ", Toast.LENGTH_LONG).show()

            drawableImageScale = getScale(viewHeight, drawableImageHeight)

            val newImageWidth = drawableImageWidth * drawableImageScale

            val expansion = (newImageWidth - viewWidth) / 2

            rectF.set(-expansion, 0f, viewWidth + expansion, viewHeight)

        } else if (getImageSizeRatio() == 1f) { //The rawImageWidth and rawImageHeight are equals
            rectF.set(0f, 0f, viewWidth, viewHeight)
        } else {//The smallest side of the image is rawImageWidth
            Toast.makeText(context, ">= 1 ", Toast.LENGTH_LONG).show()

            drawableImageScale = getScale(viewHeight, drawableImageWidth)

            val newImageHeight = drawableImageHeight * drawableImageScale

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

        //Detector handling event like zoom in or zoom out on the drawable image
        scaleGestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                rawX = event.rawX
                rawY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                dxAtEachNewPoint = event.rawX - rawX
                dyAtEachNewPoint = event.rawY - rawY

                dxAtEachNewPoint = applyOverScrollFix(dxAtEachNewPoint, measureOverScrollX())
                dyAtEachNewPoint = applyOverScrollFix(dyAtEachNewPoint, measureOverScrollY())

                rectF.left += dxAtEachNewPoint
                rectF.right += dxAtEachNewPoint

                rectF.top += dyAtEachNewPoint
                rectF.bottom += dyAtEachNewPoint

                invalidate()

                //save the last position for getting the new displacement
                rawX = event.rawX
                rawY = event.rawY
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
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
     * This listener reacts when the user touch the drawable image with 2 or more fingers
     */
    private var onScaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {

            //tiendre a crecer
            // overScale = drawableImageScale / 0.8

            var overScale = measureOverScale()

            //Log.e("FACTOR" , "FACTOR " + detector.scaleFactor)
            //Log.e("FOCUSX" , "FOCUSX " + detector.focusX)
            //Log.e("FOCUSY" , "FOCUSY " + detector.focusY)

            Log.e("OVERSCALE" , "OVERSCALE " + overScale)

            var scale = applyOverScaleFix(detector.scaleFactor, overScale)

            drawableImageScale = drawableImageScale * scale

            //setScaleKeepingFocus(detector.focusX, detector.focusY)




            invalidate()
            return true
        }

    }

    private val MAXIMUM_OVER_SCALE = 0.7f

    private fun applyOverScaleFix(scaleFactor: Float, overScale: Float): Float {
        var mOverScale = overScale
        var mScaleFactor = scaleFactor

        if (mOverScale == 1f) {
            return mScaleFactor
        }

        if (mOverScale > 1) {
            mOverScale = 1f / mOverScale
        }

        val wentOverScaleRatio = (overScale - MAXIMUM_OVER_SCALE) / (1 - MAXIMUM_OVER_SCALE)

        mScaleFactor *= wentOverScaleRatio + (1 - wentOverScaleRatio) / scaleFactor

        return scaleFactor
    }

    private fun measureOverScale(): Float {
        if (drawableImageScale < 0.83f) {
            return drawableImageScale / 0.83f
        }

        if (drawableImageScale > 0.83f) {
            return drawableImageScale / 0.83f
        }

        return 1f
    }

    private fun setScaleKeepingFocus(focusX: Float, focusY: Float) {

        val focusRatioX = (focusX - rectF.left) / rectF.width()
        val focusRatioY = (focusY - rectF.top) / rectF.height()

        val scaledFocusX = rectF.left + focusRatioX * rectF.width()
        val scaledFocusY = rectF.top + focusRatioY * rectF.height()

        //LogUtil.e("SCALE", "SCALE ${focusX - scaledFocusX}")

        rectF.left += focusX - scaledFocusX
        rectF.top += focusY - scaledFocusY

    }
}


