package com.gmail.maystruks08.nfcruntracker.ui.views

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import kotlin.math.cos
import kotlin.math.sin


class ChartView : View {

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        render(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        render(attrs)
    }

    constructor(context: Context) : super(context) {
        render(null)
    }

    private fun render(attrs: AttributeSet?) {
        attrs?.let {
            with(context.obtainStyledAttributes(it, R.styleable.ChartView)) {
                strokeWidth = getDimensionPixelSize(R.styleable.ChartView_strokeWidth, 3).toFloat()
                paintColor = getColor(R.styleable.ChartView_paintColor, Color.WHITE)
                circlePadding = getDimensionPixelSize(R.styleable.ChartView_circlePadding, 10).toFloat()
                textPadding= getDimensionPixelSize(R.styleable.ChartView_textPadding, 10).toFloat()
                textSize = getDimensionPixelSize(R.styleable.ChartView_textSize, 14).toFloat()
                recycle()
            }
        }
    }

    private val chart: Chart = Chart().apply {
        chartItems.add(ChartItem("12", R.color.colorWhite, R.color.colorGreen, 0))
        chartItems.add(ChartItem("28", R.color.colorWhite, R.color.colorRed, 0))
        chartItems.add(ChartItem("34", R.color.colorWhite, R.color.design_default_color_primary, 0))
    }

    var strokeWidth: Float = 15f
        set(value) {
            field = value
            _paint.strokeWidth = strokeWidth
            invalidate()
        }

    var paintColor = Color.TRANSPARENT
        set(value) {
            field = value
            _paint.color = value
            invalidate()
        }

    var circlePadding = 30f
        set(value) {
            field = value
            requestLayout()
        }

    var textPadding = 0f
        set(value) {
            field = value
            requestLayout()
        }

    var textSize = 30f
        set(value) {
            field = value
            invalidate()
        }

    fun setChartItems(chartItems: Array<out ChartItem>) {
        chart.chartItems.clear()
        chart.chartItems.addAll(chartItems.filter { it.progress > 0 })
        requestLayout()
    }

    private var _startAngle = 90f
    private var _minDiameter: Int = 30
    private var _radius = 40f
    private val _textBounds = Rect()
    private var _rectF: RectF = RectF()
    private var _paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        chart.getRecalculatedChartItems().forEach {
            drawSegment(canvas, it.startAngle, it.endAngle, it.backgroundColor, strokeWidth)
            drawText(
                canvas,
                it.text,
                textSize,
                it.textColor,
                it.startAngle + (it.endAngle - it.startAngle) / 2
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        _minDiameter = width.coerceAtMost(height)
        setMeasuredDimension(_minDiameter, _minDiameter)
        _radius = (_minDiameter / 2).toFloat()
        _rectF.set(
            0f + circlePadding,
            0f + circlePadding,
            _minDiameter.toFloat() - circlePadding,
            _minDiameter.toFloat() - circlePadding
        )
    }

    private fun drawSegment(
        canvas: Canvas,
        startAngle: Float,
        endAngle: Float,
        @ColorRes colorRes: Int,
        strokeWidth: Float
    ) {
        _paint.color = ContextCompat.getColor(context, colorRes)
        _paint.style = Paint.Style.STROKE
        _paint.strokeWidth = strokeWidth
        canvas.drawArc(_rectF, startAngle, endAngle - startAngle, false, _paint)
    }


    private fun drawText(
        canvas: Canvas,
        text: String,
        textSize: Float,
        @ColorRes colorRes: Int,
        angle: Float
    ) {
        _paint.color = ContextCompat.getColor(context, colorRes)
        _paint.style = Paint.Style.FILL
        _paint.isAntiAlias = true
        _paint.textSize = textSize
        _paint.getTextBounds(text, 0, text.length, _textBounds)

        val textWidth = _textBounds.width()
        val textHeight = _textBounds.height()

        val x = width.toFloat() / 2
        val y = height.toFloat() / 2

        canvas.rotate(_startAngle + angle, x, y)
        canvas.drawText(text, x - textWidth / 2, (textHeight / 2 + circlePadding + textPadding), _paint)
        canvas.rotate(-_startAngle - angle, x, y)
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
                val progressInPercent = it.progress / progressSum * 100
                val endAngleInDegrease = startAngleInDegrease + (progressInPercent / 100 * 360)
                val item = ChartItemInternal(
                    it.text,
                    it.textColor,
                    it.backgroundColor,
                    startAngleInDegrease,
                    endAngleInDegrease
                )
                startAngleInDegrease = endAngleInDegrease
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
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeInt(textColor)
        parcel.writeInt(backgroundColor)
        parcel.writeInt(progress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChartItem> {
        override fun createFromParcel(parcel: Parcel): ChartItem {
            return ChartItem(parcel)
        }

        override fun newArray(size: Int): Array<ChartItem?> {
            return arrayOfNulls(size)
        }
    }
}



