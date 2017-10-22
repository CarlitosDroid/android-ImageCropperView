package com.spidev.materialimagecropper

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 10/22/17.
 */

class GriddDrawable: Drawable() {

    private val LINE_COLOR = Color.WHITE
    private val LINE_BORDER_COLOR = 0x44888888
    private val LINE_STROKE_WIDTH = 1f
    private val TIME_BEFORE_FADE: Long = 300
    private val TIME_TO_FADE: Long = 300


    private var mLineBorderPaint = Paint()

    private val mAnimator = ValueAnimator()

    private val mAlpha = 1f

    private var mLinePaint = Paint()

    init {

        mLinePaint = Paint()
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.color = LINE_COLOR
        mLinePaint.strokeWidth = LINE_STROKE_WIDTH

        mLineBorderPaint = Paint()
        mLineBorderPaint.setStyle(Paint.Style.STROKE)
        mLineBorderPaint.setColor(LINE_BORDER_COLOR)
        mLineBorderPaint.setStrokeWidth(LINE_STROKE_WIDTH)

        mAnimator.duration = TIME_TO_FADE
        mAnimator.startDelay = TIME_BEFORE_FADE
        mAnimator.setFloatValues(1F, 0F)
        //mAnimator.addUpdateListener(mAnimatorUpdateListener)
        mAnimator.interpolator = LinearInterpolator()

        //mAnimator.start()
    }
    override fun draw(canvas: Canvas?) {
        mLinePaint.alpha = Math.round(mAlpha * 255)

        val width = bounds.width()
        val height = bounds.height()

        val left = bounds.left + width / 3
        val right = left + width / 3
        val top = bounds.top + height / 3
        val bottom = top + height / 3

        canvas?.drawLine(left.toFloat() - 1, bounds.top.toFloat(), left.toFloat() - 1, bounds.bottom.toFloat(), mLinePaint)

    }

    override fun setAlpha(alpha: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}