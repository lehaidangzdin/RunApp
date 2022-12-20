package com.lhd.runapp.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lhd.runapp.R
import com.lhd.runapp.customviews.modelCustomView.ButtonBitMapClick
import com.lhd.runapp.customviews.modelCustomView.ReceiveSeekbar
import com.lhd.runapp.utils.Utils


class MySeekBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var barColor = Color.GRAY
    private var textThumbnailSize = 20f
    private var barHeight = 24F
    private var indicatorColor = Color.CYAN
    private var progressColor = Color.GREEN
    private var textThumbnail = Color.WHITE
    private val paint = Paint()
    private val paintBitmap = Paint()
    private var processWidth = 0f
    private val indicatorBitmap =
        listOf(0, R.drawable.check, R.drawable.check, R.drawable.star)
    private val indicatorBitmapCheck =
        listOf(0, R.drawable.check_selected, R.drawable.check_selected, R.drawable.star_selected)
//    private val radiusCir = 25f

    lateinit var indicatorPositions: List<Float>
    lateinit var indicatorText: List<String>
    lateinit var indicatorBitmapReceive: ArrayList<ReceiveSeekbar>

    private var marginHorizontalProgress = width * 0.2f
    private lateinit var bitmapConvert: Bitmap
    private var lsPositionBitmapTouch = ArrayList<ButtonBitMapClick>()

    private lateinit var listener: OnClickBitmapReceive


    var progress = 1F // From float from 0 to 1
        set(state) {
            field = state
            invalidate()
        }

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
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
            textThumbnailSize =
                getFloat(R.styleable.IndicatorProgressBar_textThumbnailSize, textThumbnailSize)
            barHeight = getFloat(R.styleable.IndicatorProgressBar_barHeight, barHeight)
//            progress = getFloat(R.styleable.IndicatorProgressBar_progress, progress)
            progressColor = getColor(R.styleable.IndicatorProgressBar_progressColor, progressColor)
            textThumbnail =
                getColor(R.styleable.IndicatorProgressBar_indicatorThumbnailColor, textThumbnail)
            indicatorColor =
                getColor(R.styleable.IndicatorProgressBar_indicatorColor, indicatorColor)
            recycle()
        }
    }

    /**
     * click listener receive
     */
    fun setOnClickListener(listener: OnClickBitmapReceive) {
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        lsPositionBitmapTouch.clear()
        drawProgressBar(canvas)
        drawProgress(canvas)
        drawIndicators(canvas)
    }

    /**
     * ve bg seekbar
     * */
    private fun drawProgressBar(canvas: Canvas) {
        paint.color = barColor
        drawCenteredBar(canvas, marginHorizontalProgress, width.toFloat())
    }

    /**
     * ve line mau do
     * */
    private fun drawProgress(canvas: Canvas) {
        paint.color = progressColor
        processWidth =
            if (progress >= 1) {
                width.toFloat()
            } else {
                (progress) * width
            }
        drawCenteredBar(canvas, marginHorizontalProgress, processWidth)
    }

    private fun drawCenteredBar(canvas: Canvas, left: Float, right: Float) {
        val barTop = (measuredHeight - barHeight) / 2
        val barBottom = (measuredHeight + barHeight) / 2

        val barRect = RectF(left, barTop, right, barBottom)
        canvas.drawRoundRect(barRect, 50F, 50F, paint)
    }

    /**
     * ve hinh tron check
     * */
    private fun drawIndicators(canvas: Canvas) {
        indicatorPositions.forEachIndexed { index, element ->
            val barPositionCenter = element * width
            var ls = indicatorBitmap
            var isActive = false

            if (processWidth >= barPositionCenter) {
                ls = indicatorBitmapCheck
                paintBitmap.alpha = 200
                isActive = true
            } else {
                paintBitmap.alpha = 60
            }
            if (indicatorBitmapReceive[index].isDisable) {
                paintBitmap.alpha = 60
            }
            drawCir(canvas, barPositionCenter, ls, index)
            drawThumbnail(canvas, barPositionCenter, index)
            drawIndicatorsReceive(
                canvas,
                barPositionCenter,
                isActive,
                indicatorBitmapReceive[index].icon,
                paintBitmap
            )
        }
    }

    private fun getRadiusCir(): Float {

        val res: Resources = resources
        val bitmap = BitmapFactory.decodeResource(res, indicatorBitmap[1])

        return bitmap.height / 2f
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
            canvas.drawCircle(left + getRadiusCir(), center, getRadiusCir(), paint)
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
        paint.textSize = textThumbnailSize
        val bottomT = ((measuredHeight + barHeight) / 2)
        val marginTop = getRadiusCir() * 2f
        var marginLeft = 0f
        if (i == 0) {
            marginLeft = (getRadiusCir() / 2.5f)
        }
        canvas.drawText(indicatorText[i], left + marginLeft, bottomT + marginTop, paint)
    }

    /**
     * ve receive
     */
    private fun drawIndicatorsReceive(
        canvas: Canvas,
        leftDefault: Float,
        isActive: Boolean,
        icon: Int,
        paintBitmap: Paint
    ) {
        val barBottom = (measuredHeight - barHeight) / 2
        val marginTop = getRadiusCir() * 4f
        val topPositionBitmap = barBottom + marginTop
        val leftPositionBitmap = leftDefault - 55f / 2.5f

        // neu element != 0 => ve hinh receive
        val bitmap = BitmapFactory.decodeResource(resources, icon)

        if (bitmap != null) {
            bitmapConvert = Utils.resizeBitmap(bitmap, 45, 40)
            canvas.drawBitmap(bitmapConvert, leftPositionBitmap, topPositionBitmap, paintBitmap)
        }

        /**
         * neu isActive = true => add position có thể click vào list lsPositionBitmapTouch
         */
        if (isActive) {
            lsPositionBitmapTouch.add(
                ButtonBitMapClick(
                    leftPositionBitmap,
                    topPositionBitmap
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                /***
                 * for lsPositionBitmapTouch để lấy position click của bitmap && bitmap isDisable = false => click
                 */
                lsPositionBitmapTouch.forEachIndexed { index, posBitmapTouch ->
                    //Check if the x and y position of the touch is inside the bitmap
                    if (index != 0
                        && (x > posBitmapTouch.xPosition
                                && x < posBitmapTouch.xPosition + bitmapConvert.width
                                && y > posBitmapTouch.yPosition
                                && y < posBitmapTouch.yPosition + bitmapConvert.height)
                        && !indicatorBitmapReceive[index].isDisable
                    ) {
                        //Bitmap touched
                        listener.clickItem(index)
                    }
                }
            }
        }
        return false
    }


    interface OnClickBitmapReceive {
        fun clickItem(index: Int) {}
    }
}


