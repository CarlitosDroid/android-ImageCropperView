package com.spidev.materialimagecropper

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator

/**
 * This class draw the grid over the drawable image in the class {@link ImageCropperView}
 *
 * @author Carlos Leonardo Camilo Vargas Huamán on 10/22/17.
 * @see Drawable
 * @see Drawable.setCallback
 * @see Drawable.invalidateSelf
 * @see Paint
 * @since 1.0.0
 * @version 1.0.1
 */

class GridDrawable : Drawable() {

    // Class name for log
    private val TAG = GridDrawable::class.java.simpleName

    /**
     * Configuring alpha animation of the grid(0-255)
     */
    private var mAlpha = 1f

    /**
     * Lines and border lines paint for drawing the grid
     */
    var linePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 1F
    }
    var lineBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.GREEN
        strokeWidth = 1F
    }

    private val animatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        mAlpha = animation?.animatedValue as Float
        invalidateSelf()
    }

    /**
     * Animator for handling fade in and fade out animations
     */
    private val valueAnimator = ValueAnimator().apply {
        duration = 300
        startDelay = 300
        setFloatValues(1F, 0F)
        addUpdateListener(animatorUpdateListener)
        interpolator = LinearInterpolator()
        start()
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        mAlpha = 1f
        valueAnimator.cancel()
        valueAnimator.start()
    }

    /**
     * Necessarily if we want to see the grid drawn we need to call this method
     * and pass the {@link Canvas}.
     * Don't forget this method is not called when we create an instance of this class.
     */
    override fun draw(canvas: Canvas) {

        alpha = Math.round(mAlpha * 255)

        val width = bounds.width()
        val height = bounds.height()

        val startXFirstVerticalLine = bounds.left.toFloat() + width / 3
        val startXSecondVerticalLine = startXFirstVerticalLine + width / 3
        val startYFirstHorizontalLine = bounds.top.toFloat() + height / 3
        val startYSecondHorizontalLine = startYFirstHorizontalLine + height / 3

        //VERTICAL
        //Drawing first vertical line
        canvas.drawLine(startXFirstVerticalLine, bounds.top.toFloat(), startXFirstVerticalLine, bounds.bottom.toFloat(), linePaint)

        //Drawing borders of the first vertical line
        canvas.drawLine(
                startXFirstVerticalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2), bounds.top.toFloat(),
                startXFirstVerticalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2), bounds.bottom.toFloat(),
                lineBorderPaint)
        canvas.drawLine(
                startXFirstVerticalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2), bounds.top.toFloat(),
                startXFirstVerticalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2), bounds.bottom.toFloat(),
                lineBorderPaint)

        //Drawing second vertical line
        canvas.drawLine(startXSecondVerticalLine, bounds.top.toFloat(), startXSecondVerticalLine, bounds.bottom.toFloat(), linePaint)

        //Drawing borders of the second vertical line
        canvas.drawLine(
                startXSecondVerticalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2), bounds.top.toFloat(),
                startXSecondVerticalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2), bounds.bottom.toFloat(),
                lineBorderPaint)
        canvas.drawLine(
                startXSecondVerticalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2), bounds.top.toFloat(),
                startXSecondVerticalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2), bounds.bottom.toFloat(),
                lineBorderPaint)

        //HORIZONTAL
        //Drawing first horizontal line
        canvas.drawLine(bounds.left.toFloat(), startYFirstHorizontalLine, bounds.right.toFloat(), startYFirstHorizontalLine, linePaint)

        //Drawing borders of the first horizontal line
        canvas.drawLine(
                bounds.left.toFloat(), startYFirstHorizontalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2),
                bounds.right.toFloat(), startYFirstHorizontalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2),
                lineBorderPaint)
        canvas.drawLine(
                bounds.left.toFloat(), startYFirstHorizontalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2),
                bounds.right.toFloat(), startYFirstHorizontalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2),
                lineBorderPaint)

        //Drawing second horizontal line
        canvas.drawLine(bounds.left.toFloat(), startYSecondHorizontalLine, bounds.right.toFloat(), startYSecondHorizontalLine, linePaint)

        //Drawing borders of the second horizontal line
        canvas.drawLine(
                bounds.left.toFloat(), startYSecondHorizontalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2),
                bounds.right.toFloat(), startYSecondHorizontalLine - (linePaint.strokeWidth / 2) - (lineBorderPaint.strokeWidth / 2),
                lineBorderPaint)
        canvas.drawLine(
                bounds.left.toFloat(), startYSecondHorizontalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2),
                bounds.right.toFloat(), startYSecondHorizontalLine + (linePaint.strokeWidth / 2) + (lineBorderPaint.strokeWidth / 2),
                lineBorderPaint)
    }

    override fun setAlpha(alpha: Int) {
        linePaint.alpha = alpha
        lineBorderPaint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}