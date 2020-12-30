package com.gmail.maystruks08.nfcruntracker.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import kotlin.math.cos
import kotlin.math.sin


class ChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val chart: Chart = Chart().apply {
        chartItems.add(ChartItem("Text", R.color.colorText, R.color.colorPrimary, 50))
        chartItems.add(ChartItem("Text", R.color.colorText, R.color.colorGreen, 25))
        chartItems.add(ChartItem("Text", R.color.colorText, R.color.colorRed, 25))
    }

    private var minDiameter: Int = 30
    private var radius = 40f
    private var paintColor = Color.TRANSPARENT
    private var rectF: RectF = RectF()
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var strokeWidth = 15f

    fun setChartItems(chartItems: List<ChartItem>) {
        chart.chartItems.clear()
        chart.chartItems.addAll(chartItems)
        invalidate()
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        paint.strokeWidth = strokeWidth
        requestLayout()
    }

    fun setColor(color: Int) {
        this.paintColor = color
        paint.color = color
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rectF.set(
            0f + strokeWidth / 2,
            0f + strokeWidth / 2,
            minDiameter.toFloat() - strokeWidth / 2,
            minDiameter.toFloat() - strokeWidth / 2
        )

        chart.getRecalculatedChartItems().forEach {
            drawSegment(canvas, it.startAngle, it.endAngle, it.backgroundColor, strokeWidth)
        }

        drawText(canvas, "97%", 16f, R.color.colorBlack, 0f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        minDiameter = width.coerceAtMost(height)
        setMeasuredDimension(minDiameter, minDiameter)
        radius = (minDiameter / 2).toFloat()
        rectF.set(
            0f + strokeWidth / 2,
            0f + strokeWidth / 2,
            minDiameter.toFloat() / 2,
            minDiameter.toFloat() / 2
        )
    }

    private fun drawSegment(
        canvas: Canvas,
        startAngle: Float,
        endAngle: Float,
        @ColorRes colorRes: Int,
        strokeWidth: Float
    ) {
        paint.color = ContextCompat.getColor(context, colorRes)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        canvas.drawArc(rectF, startAngle, endAngle, false, paint)
    }


    private fun drawText(
        canvas: Canvas,
        text: String,
        textSize: Float,
        @ColorRes colorRes: Int,
        angle: Float
    ) {
        paint.color = ContextCompat.getColor(context, colorRes)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.textSize = textSize

        canvas.rotate(angle, (minDiameter / 2).toFloat(), (minDiameter / 2).toFloat())
        canvas.drawText(text, (minDiameter / 2).toFloat(), (minDiameter / 2).toFloat(), paint)
        canvas.rotate(-angle, (minDiameter / 2).toFloat(), (minDiameter / 2).toFloat())
    }

    private fun findPointInCircle(radius: Float, angle: Float): Pair<Float, Float> {
        val x = radius * cos(angle)
        val y = radius * sin(angle)
        return x to y
    }

    internal data class Chart(val chartItems: MutableList<ChartItem> = mutableListOf()) {

        fun getRecalculatedChartItems(): List<ChartItemInternal> {
            val progressSum = chartItems.sumBy { it.progress }.toFloat()
            var startAngleInDegrease = 0f
            return chartItems.map {
                val progressInPercent = it.progress / progressSum * 100f
                val endAngleInDegrease = (progressInPercent / 100f * 360)
                val item = ChartItemInternal(
                    it.text,
                    it.textColor,
                    it.backgroundColor,
                    startAngleInDegrease,
                    endAngleInDegrease
                )
                startAngleInDegrease += endAngleInDegrease
                return@map item
            }
        }
    }

    internal data class ChartItemInternal(
        val text: String,
        @ColorRes val textColor: Int,
        @ColorRes val backgroundColor: Int,
        val startAngle: Float,
        val endAngle: Float
    )
}


data class ChartItem(
    val text: String,
    @ColorRes val textColor: Int,
    @ColorRes val backgroundColor: Int,
    val progress: Int
)



