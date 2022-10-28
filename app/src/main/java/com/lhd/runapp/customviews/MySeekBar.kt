package com.lhd.runapp.customviews

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.lhd.runapp.R
import com.lhd.runapp.customviews.modelCustomView.PositionBitMapClick
import com.lhd.runapp.utils.Utils


class MySeekBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var barColor = Color.GRAY
    private var barHeight = 25F
    private var indicatorColor = Color.CYAN
    private var progressColor = Color.GREEN
    private var textThumbnail = Color.WHITE
    private val paint = Paint()
    private val paintBitmap = Paint()

    private var processWidth = 0f
    private val indicatorBitmap =
        listOf(0, R.drawable.check, R.drawable.check, R.drawable.starcheck)
    private val indicatorBitmapCheck =
        listOf(0, R.drawable.check_, R.drawable.check_, R.drawable.star_check_)
    private val indicatorBitmapReceive =
        listOf(0, R.drawable.reiceve1, R.drawable.receive2, R.drawable.reiceve3)

    private val radiusCir = 25f

    lateinit var indicatorPositions: List<Float>
    lateinit var indicatorText: List<String>
    private var marginHorizontalProgress = width * 0.1f
    private lateinit var bitmapConvert: Bitmap
    private var lsPositionBitmapTouch = ArrayList<PositionBitMapClick>()

    private lateinit var listener: OnClickBitmapReceive

    private var progress = 1F // From float from 0 to 1
        set(state) {
            field = state
            invalidate()
        }

    init {
        paint.isAntiAlias = true
        paintBitmap.style = Paint.Style.FILL
        paintBitmap.alpha = 60
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.IndicatorProgressBar,
            0, 0
        ).apply {
            barColor = getColor(R.styleable.IndicatorProgressBar_barColor, barColor)
            barHeight = getFloat(R.styleable.IndicatorProgressBar_barHeight, barHeight)
            progress = getFloat(R.styleable.IndicatorProgressBar_progress, progress)
            progressColor = getColor(R.styleable.IndicatorProgressBar_progressColor, progressColor)
            textThumbnail =
                getColor(R.styleable.IndicatorProgressBar_indicatorThumbnailColor, textThumbnail)
            indicatorColor =
                getColor(R.styleable.IndicatorProgressBar_indicatorColor, indicatorColor)
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL // We will only use FILL for the progress bar's components.

        drawProgressBar(canvas)
        drawProgress(canvas)
        drawIndicators(canvas)
    }


    /**
     * Used to get the measuredWidth from the view as a float to be used in the draw methods.
     */
    private fun width(): Float {
        return measuredWidth.toFloat()
    }

    /**
     * ve bg seekbar
     * */
    private fun drawProgressBar(canvas: Canvas) {
        paint.color = barColor
        drawCenteredBar(canvas, marginHorizontalProgress, width())
    }

    /**
     * ve line mau do
     * */
    private fun drawProgress(canvas: Canvas) {
        paint.color = progressColor
        processWidth = (progress) * width()
        drawCenteredBar(canvas, marginHorizontalProgress, processWidth)
    }

    /**
     * ve hinh tron check
     * */
    private fun drawIndicators(canvas: Canvas) {
        indicatorPositions.forEachIndexed { index, element ->
            val barPositionCenter = element * width()
            var ls = indicatorBitmap
            var isEnabled: Boolean
            if (processWidth >= barPositionCenter) {
                ls = indicatorBitmapCheck
                paintBitmap.alpha = 200
                isEnabled = true
            } else {
                paintBitmap.alpha = 60
                isEnabled = false
            }
            drawCir(canvas, barPositionCenter, ls, index)
            drawThumbnail(canvas, barPositionCenter, index)
            drawIndicatorsReceive(
                canvas,
                barPositionCenter,
                indicatorBitmapReceive,
                index,
                isEnabled,
                paintBitmap
            )

        }
    }

    private fun drawCenteredBar(canvas: Canvas, left: Float, right: Float) {
        val barTop = (measuredHeight - barHeight) / 2
        val barBottom = (measuredHeight + barHeight) / 2

        val barRect = RectF(left, barTop, right, barBottom)
        canvas.drawRoundRect(barRect, 50F, 50F, paint)
    }

    /**
     * ve hinh tron
     * */
    private fun drawCir(canvas: Canvas, left: Float, ls: List<Int>, index: Int) {
        val barTop = (measuredHeight - barHeight) / 2
        val barBottom = ((measuredHeight + barHeight) / 2)
        val center = (barTop + barBottom) / 2

        if (ls[index] == 0) {
            // neu element = 0 => ve hinh tron do
            paint.color = Color.RED
            canvas.drawCircle(left + radiusCir, center, radiusCir, paint)
        } else {
            // neu element != 0 => ve hinh check(star)
            val res: Resources = resources
            val bitmap = BitmapFactory.decodeResource(res, ls[index])
            val top = (height - bitmap.height) / 2f
            canvas.drawBitmap(bitmap, left, top, paint)
        }
    }

    /**
     * ve text
     * */
    private fun drawThumbnail(canvas: Canvas, left: Float, i: Int) {
        paint.color = textThumbnail
        paint.textSize = 26f
        val bottomT = ((measuredHeight + barHeight) / 2)
        val marginTop = width() * 0.1f
        var marginLeft = 0f
        if (i == 0) {
            marginLeft = (radiusCir / 2f)
        }
        canvas.drawText(indicatorText[i], left + marginLeft, bottomT + marginTop, paint)
    }

    /**
     * ve receive
     */
    private fun drawIndicatorsReceive(
        canvas: Canvas,
        leftDefault: Float,
        ls: List<Int>,
        index: Int,
        isEnabled: Boolean,
        paintBitmap: Paint
    ) {
        val barBottom = (measuredHeight - barHeight) / 2

        if (ls[index] == 0) {
            return
        }
        // neu element != 0 => ve hinh receive
        val res: Resources = resources
        val bitmap = BitmapFactory.decodeResource(res, ls[index])

        bitmapConvert = Utils.resizeBitmap(bitmap, 60, 40)

        val marginTop = height * 0.3f
        val topPositionBitmap = barBottom + marginTop
        val leftPositionBitmap = leftDefault - bitmapConvert.width / 4.5f

        if (isEnabled) {
            lsPositionBitmapTouch.add(PositionBitMapClick(leftPositionBitmap, topPositionBitmap))
        }
        canvas.drawBitmap(bitmapConvert, leftPositionBitmap, topPositionBitmap, paintBitmap)
    }

    fun setOnClickListener(listener: OnClickBitmapReceive) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lsPositionBitmapTouch.forEachIndexed { index, position ->
                    //Check if the x and y position of the touch is inside the bitmap
                    if (x > position.xPosition
                        && x < position.xPosition + bitmapConvert.width
                        && y > position.yPosition
                        && y < position.yPosition + bitmapConvert.height) {
                        //Bitmap touched
                        Log.e("BITMAP_CLICKED", "onTouchEvent: bit map $index")
                        listener.clickItem(index)
                    }
                }
            }
        }
        return false
    }

    interface OnClickBitmapReceive {
        fun clickItem(positionReceive: Int)
    }
}


