package com.yf.accelerator.update.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.widget.ProgressBar
import java.math.BigDecimal


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/1/17 14:52
+--------------------------------------+
+ Des:
+--------------------------------------+
 */

class TextFollowingProgressBar : ProgressBar {

    private val textPaint: Paint by lazy { Paint() }
    private var text: String? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initPaint()
    }

    private fun initPaint() {
        textPaint.run {
            color = Color.parseColor("#333333")
            textSize = dp2px(12f)
            isAntiAlias = true
        }
    }

    override fun setProgress(progress: Int) {
        text = "$progress%"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.setProgress(progress.toDouble().toInt(), false)
        } else {
            super.setProgress(progress.toDouble().toInt())
        }
    }

    fun dp2px(dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun sp2px(sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val rect = Rect()
        text?.let {
            textPaint.getTextBounds(it, 0, it.length, rect)
            // 获取文字高度
            val y = (height / 2) - rect.centerY()
            if (progress in 0..9) {
                canvas?.drawText(it, 0f, y.toFloat(), textPaint)
            } else {
            //获取一份宽度
            val pro =
                BigDecimal(width / 100 * progress).setScale(0, BigDecimal.ROUND_HALF_UP)
            val x = pro.toInt() - rect.centerX() * 2
            canvas?.drawText(it, x.toFloat(), y.toFloat(), textPaint)

            }
        }
    }
}