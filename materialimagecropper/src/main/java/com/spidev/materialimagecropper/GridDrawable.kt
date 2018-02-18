package com.spidev.materialimagecropper

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 10/22/17.
 */

class GridDrawable : Drawable() {

    // Class name for log
    private val TAG = GridDrawable::class.java.simpleName

    //Values for animations
    private val TIME_BEFORE_FADE: Long = 300
    private val TIME_TO_FADE: Long = 300
    private val mAnimator = ValueAnimator()

    //Paint for drawing the line and line border
    var mLinePaint = Paint()
    private var mLineBorderPaint = Paint()

    //Properties for grid's line
    private val LINE_COLOR = Color.WHITE
    private val LINE_STROKE_WIDTH = 1f

    //Properties for grid line border
    private val LINE_BORDER_COLOR = Color.RED
    private val LINE_BORDER_STROKE_WIDTH = 1f

    private var mAlpha = 1f

    init {
        //Configuring grid's lines
        mLinePaint = Paint()
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.color = LINE_COLOR
        mLinePaint.strokeWidth = LINE_STROKE_WIDTH

        //Configuring up grid line borders
        mLineBorderPaint = Paint()
        mLineBorderPaint.style = Paint.Style.STROKE
        mLineBorderPaint.color = LINE_BORDER_COLOR
        mLineBorderPaint.strokeWidth = LINE_BORDER_STROKE_WIDTH

        //Configuring animations of the grid
        mAnimator.duration = TIME_TO_FADE
        mAnimator.startDelay = TIME_BEFORE_FADE
        mAnimator.setFloatValues(1F, 0F)
        mAnimator.addUpdateListener { animation ->

            mAlpha = animation.animatedValue as Float
            // LogUtil.e(TAG, "ANIMATOR  $mAlpha")
            mLinePaint.alpha = Math.round(mAlpha * 255)
            invalidateSelf()
        }
        //mAnimator.interpolator = LinearInterpolator()
        //mAnimator.start()
    }

    override fun setBounds(bounds: Rect?) {
        super.setBounds(bounds)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas?) {


        /*LogUtil.e(TAG, "ANIMATOR2  ${mAlpha * 255}")
        mLinePaint.alpha = Math.round(mAlpha * 255)
        mLineBorderPaint.alpha = Math.round(mAlpha * 0x44)*/

        val width = bounds.width()
        val height = bounds.height()

        //LogUtil.e(TAG, "width  $width")
        //LogUtil.e(TAG, "height  $height")
        //Basically bounds.left, bounds.right, bounds.top, bounds.bottom are the coordinates*/

        val startXFirstVerticalLine = bounds.left.toFloat() + width / 3
        val startXSecondVerticalLine = startXFirstVerticalLine + width / 3
        val startYFirstHorizontalLine = bounds.top.toFloat() + height / 3
        val startYSecondHorizontalLine = startYFirstHorizontalLine + height / 3

        val thirdOfTheWidth = width / 3
        val thirdOfTheHeight = height / 3


        //Drawing borders of the first vertical line
        canvas?.drawLine(startXFirstVerticalLine - 1, bounds.top.toFloat(), startXFirstVerticalLine - 1, bounds.bottom.toFloat(), mLineBorderPaint)
        canvas?.drawLine(startXFirstVerticalLine + 1, bounds.top.toFloat(), startXFirstVerticalLine + 1, bounds.bottom.toFloat(), mLineBorderPaint)

        //Drawing borders of the second vertical line
        canvas?.drawLine(startXSecondVerticalLine - 1, bounds.top.toFloat(), startXSecondVerticalLine - 1, bounds.bottom.toFloat(), mLineBorderPaint)
        canvas?.drawLine(startXSecondVerticalLine + 1, bounds.top.toFloat(), startXSecondVerticalLine + 1, bounds.bottom.toFloat(), mLineBorderPaint)

        //Drawing borders of the first horizontal line
        canvas?.drawLine(bounds.left.toFloat(), startYFirstHorizontalLine - 1, bounds.right.toFloat(), startYFirstHorizontalLine - 1, mLineBorderPaint)
        canvas?.drawLine(bounds.left.toFloat(), startYFirstHorizontalLine + 1, bounds.right.toFloat(), startYFirstHorizontalLine + 1, mLineBorderPaint)

        //Drawing borders of the second horizontal line
        canvas?.drawLine(bounds.left.toFloat(), startYSecondHorizontalLine - 1, bounds.right.toFloat(), startYSecondHorizontalLine - 1, mLineBorderPaint)
        canvas?.drawLine(bounds.left.toFloat(), startYSecondHorizontalLine + 1, bounds.right.toFloat(), startYSecondHorizontalLine + 1, mLineBorderPaint)

        //Drawing vertical grid lines
        canvas?.drawLine(startXFirstVerticalLine, bounds.top.toFloat(), startXFirstVerticalLine, bounds.bottom.toFloat(), mLinePaint)


/*
        //Drawing vertical grid lines
        canvas?.drawLine(thirdOfTheWidth.toFloat(), 0f, thirdOfTheWidth.toFloat(), height.toFloat(), mLinePaint)
        canvas?.drawLine((thirdOfTheWidth * 2).toFloat(), 0f, (thirdOfTheWidth * 2).toFloat(), height.toFloat(), mLinePaint)
        //Drawing horizontal grid lines
        canvas?.drawLine(0f, thirdOfTheHeight.toFloat(), width.toFloat(), thirdOfTheHeight.toFloat(), mLinePaint)
        canvas?.drawLine(0f, (thirdOfTheHeight * 2).toFloat(), width.toFloat(), (thirdOfTheHeight * 2).toFloat(), mLinePaint)*/

    }

    override fun setAlpha(alpha: Int) {
        LogUtil.e(TAG, "alpha $alpha")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setCentralLineColor(colorResource: Int) {
        mLinePaint.color = colorResource
    }
}