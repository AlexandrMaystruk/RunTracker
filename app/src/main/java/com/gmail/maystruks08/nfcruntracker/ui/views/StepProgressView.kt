package com.gmail.maystruks08.nfcruntracker.ui.views

import android.content.Context
import android.graphics.drawable.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.*
import com.gmail.maystruks08.nfcruntracker.R

class StepProgressView : ViewGroup {

    private lateinit var arcActiveDrawable: ColorDrawable
    private lateinit var arcInactiveDrawable: ColorDrawable
    private lateinit var doneDrawable: Drawable
    private lateinit var doneWarningDrawable: Drawable
    private lateinit var undoneDrawable: Drawable
    private lateinit var currentDrawable: Drawable

    @DrawableRes
    private var doneDrawableId: Int = R.drawable.ic_check_circle

    @DrawableRes
    private var doneWarningDrawableId: Int = R.drawable.ic_check_warning

    @DrawableRes
    private var undoneDrawableId: Int = R.drawable.ic_unchecked

    @DrawableRes
    private var currentDrawableId: Int = R.drawable.ic_checked

    @ColorInt
    private var textNodeTitleColor = ContextCompat.getColor(context, R.color.colorPrimary)

    @ColorInt
    private var connectionLineColor = ContextCompat.getColor(context, R.color.colorGreen)

    @ColorInt
    private var colorInactive = ContextCompat.getColor(context, R.color.colorAccent)

    private var beansList = listOf(
        Bean("C", StepState.CURRENT),
        Bean("15", StepState.UNDONE),
        Bean("40", StepState.UNDONE),
        Bean("70", StepState.UNDONE),
        Bean("Ф", StepState.UNDONE)
    )
    private var currentStep = 0
    private var stepsCount = 0

    private var nodeHeight = -1f
    private var textNodeTitleSize = resources.getDimension(R.dimen.text_m).toInt()
    private var textNodeSize = resources.getDimension(R.dimen.size_s).toInt()
    private var textTitlePadding = toPx(5f, context)
    private var arcHeight = toPx(2f, context)
    private val minSpacingLength = toPx(5, context)
    private val nodeDefaultRatio = 0.1
    private val arcsMaxRatio = 0.60
    private val arcTransitionDuration = 200

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.StepProgressView, 0, 0).apply {
            try {
                colorInactive = getColor(R.styleable.StepProgressView_colorInactive, colorInactive)
                //node setup

                doneDrawableId = getResourceId(R.styleable.StepProgressView_nodeDoneDrawable, doneDrawableId)
                undoneDrawableId = getResourceId(R.styleable.StepProgressView_nodeUndoneDrawable, undoneDrawableId)
                currentDrawableId =getResourceId( R.styleable.StepProgressView_nodeCurrentDrawable, currentDrawableId)

                nodeHeight = getDimension(R.styleable.StepProgressView_nodeHeight, nodeHeight)
                //arc setup
                arcHeight = getDimension(R.styleable.StepProgressView_arcWidth, arcHeight)
                connectionLineColor = getColor(R.styleable.StepProgressView_arcColor, connectionLineColor)

                textTitlePadding = getDimension(R.styleable.StepProgressView_textTitlePadding, textTitlePadding)
                textNodeTitleSize = getDimensionPixelSize(R.styleable.StepProgressView_textNodeTitleSize, textNodeTitleSize)
                textNodeSize = getDimensionPixelSize(R.styleable.StepProgressView_textNodeSize, textNodeSize)
                textNodeTitleColor = getColor(R.styleable.StepProgressView_textNodeTitleColor, textNodeTitleColor)
            } finally {
                recycle()
            }
        }
        init()
    }

    private fun init() {
        doneDrawable = ContextCompat.getDrawable(context, doneDrawableId) ?: createStrokeOvalDrawable(context, textNodeTitleColor)
        doneWarningDrawable = ContextCompat.getDrawable(context, doneWarningDrawableId) ?: createStrokeOvalDrawable(context, textNodeTitleColor)
        undoneDrawable = ContextCompat.getDrawable(context, undoneDrawableId) ?: createStrokeOvalDrawable(context, textNodeTitleColor)
        currentDrawable = ContextCompat.getDrawable(context, currentDrawableId) ?: createStrokeOvalDrawable(context, textNodeTitleColor)
        arcActiveDrawable = ColorDrawable(connectionLineColor)
        arcInactiveDrawable = ColorDrawable(colorInactive)
        createViews()
    }

    private fun createViews() {
        stepsCount = beansList.size
        if (stepsCount == 0) {
            return
        }
        removeAllViews()
        for (i in 0 until stepsCount) {
            addView(textViewForStepTitle(i))
            addView(textViewForStep(i, i == 0))
            if (i != stepsCount - 1) {
                addView(arcView(i))
            }
        }
    }

    //Global variables to save measuring results
    private var titleTextMaxHeight = 0
    private var titleTextMaxWidth = 0
    private var textOverflow = 0

    //flag to show if overflow mode was apply to view
    //overflow happen when requested nodes size does not fit into layout
    private var hasNodeOverflow = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        //Calculates width where views could be actually drawn
        val w = if (wMode != MeasureSpec.EXACTLY) {
            //respect margins if measure spec is not exact
            wSize - paddingStart - paddingEnd - marginStart - marginEnd
        } else {
            wSize - paddingStart - paddingEnd
        }
        val nodeSize = getNodeSize(w, heightMeasureSpec)
        val arcWidth = if (stepsCount > 1) getArcWidth(widthMeasureSpec, w, nodeSize) else 0

        resolveTextOverflow(nodeSize)
        children.forEach {
            when {
                //Measure titles for step (node) views
                //Text takes a width equal to node size + allowed overflow size
                it is TextView && (it.tag as String == STEP_TITLE_TAG) -> {
                    val wSpecToMeasure =
                        MeasureSpec.makeMeasureSpec(nodeSize + textOverflow, MeasureSpec.AT_MOST)
                    val hSpecToMeasure = MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.AT_MOST)
                    it.measure(wSpecToMeasure, hSpecToMeasure)
                    //save measuring results to use them onLayout
                    val titleTextHeight = it.measuredHeight
                    val titleTextWidth = it.measuredWidth
                    if (titleTextHeight > titleTextMaxHeight) {
                        titleTextMaxHeight = titleTextHeight + textTitlePadding.toInt()
                    }
                    if (titleTextWidth > titleTextMaxWidth) {
                        titleTextMaxWidth = titleTextWidth
                    }
                }
                else -> {
                    //If children is not a text view (text view is used for nodes and titles)
                    //than it is a view for arc
                    //
                    //measures arc view
                    val arcWSpec = MeasureSpec.makeMeasureSpec(arcWidth, MeasureSpec.EXACTLY)
                    val arcHSpec =
                        MeasureSpec.makeMeasureSpec(arcHeight.toInt(), MeasureSpec.EXACTLY)
                    if (!hasNodeOverflow) {
                        (it.layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 0)
                    } else {
                        //remove margin if view is in overflow mode to escape case when arc is smaller than its margins
                        (it.layoutParams as LinearLayout.LayoutParams).setMargins(0)
                    }
                    it.measure(arcWSpec, arcHSpec)
                }
            }
        }
        //Measure node (step) views. Text view is uses to draw nodes
        children.filter { it is TextView && (it.tag as String != STEP_TITLE_TAG) }.forEach {
            //Resolve margins to fit node view with text vertically
            val nodeActualSize = if (hMode != MeasureSpec.EXACTLY) {
                (it.layoutParams as LinearLayout.LayoutParams).setMargins(
                    0,
                    titleTextMaxHeight,
                    0,
                    0
                )
                nodeSize
            } else {
                (it.layoutParams as LinearLayout.LayoutParams).setMargins(
                    titleTextMaxHeight / 2,
                    titleTextMaxHeight,
                    titleTextMaxHeight / 2,
                    0
                )
                nodeSize - titleTextMaxHeight
            }
            val nodeViewSizeSpec = MeasureSpec.makeMeasureSpec(nodeActualSize, MeasureSpec.EXACTLY)
            it.measure(nodeViewSizeSpec, nodeViewSizeSpec)
        }
        //Resolve desired width and height that step progress want to take to fit all views
        //If width or height mode are exact, parameters from specs are used
        val desiredH = if (hMode != MeasureSpec.EXACTLY) {
            nodeSize + paddingTop + paddingBottom + titleTextMaxHeight
        } else {
            hSize
        }
        val desiredW = if (wMode != MeasureSpec.EXACTLY) {
            nodeSize * stepsCount + arcWidth * (stepsCount - 1) + paddingStart + paddingEnd + textOverflow
        } else {
            wSize
        }
        setMeasuredDimension(desiredW, desiredH)
    }

    //Text could be bigger than node view
    //Text overflow margin is calculated to fit text in a node view
    private fun resolveTextOverflow(nodeSize: Int) {
        textOverflow = nodeSize
    }

    //Calculate optimal node size to fit view width.
    //If node with default or exact size does not fit layout, it scales down to fit available width
    private fun getNodeSize(width: Int, heightMeasureSpec: Int): Int {
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        return if (hMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.UNSPECIFIED) {
            val nodeDefaultSize = if (nodeHeight != -1f) {
                nodeHeight.toInt()
            } else {
                (width * nodeDefaultRatio).toInt()
            }
            //If node fits take it default size
            //If not calculate the maximal size that the node could take respecting view width
            if (nodeSizeFits(width, nodeDefaultSize)) {
                hasNodeOverflow = false
                nodeDefaultSize
            } else {
                hasNodeOverflow = true
                maximalNodeSize(width)
            }
        } else {
            hSize
        }
    }

    //Calculate optimal arcs size for steps
    //Arcs take all remaining space that is not taken by node or margins
    //If WRAP_CONTENT width is set for layout arc size could be reduced
    //to respect optimal proportions for nodes and arcs
    private fun getArcWidth(widthMeasureSpec: Int, width: Int, nodeSize: Int): Int {
        //include padding for titles
        val sCount = stepsCount + 1
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val arcsCount = stepsCount - 1
        val allArcsWidth = (width - sCount * nodeSize)
        val isArcsWidthAppropriate = allArcsWidth <= width * arcsMaxRatio
        return if (isArcsWidthAppropriate || (!isArcsWidthAppropriate && wMode == MeasureSpec.EXACTLY)) {
            allArcsWidth / arcsCount
        } else {
            (width * arcsMaxRatio).toInt() / arcsCount
        }
    }

    private fun nodeSizeFits(width: Int, desiredSize: Int): Boolean {
        //include padding for titles
        val sCount = stepsCount + 1
        return (width - desiredSize * sCount) >= minSpacingLength * (stepsCount - 1)
    }

    private fun maximalNodeSize(width: Int): Int {
        //include padding for titles
        val sCount = stepsCount + 1
        return (width - minSpacingLength * (stepsCount - 1)) / sCount
    }

    //Arranges all created views in a proper order from left to right
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //calculate insets
        var left = paddingStart + textOverflow / 2
        val top = paddingTop
        val step: View
        try {
            //Take a node view to calculate it margins
            step = children.first {
                it is TextView && it.tag as String != STEP_TITLE_TAG
            }
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            return
        }
        //Width of a node view
        val stepWidth = step.measuredWidth + step.paddingStart + step.paddingEnd
        //Layout children sequentially
        children.forEach {
            when {
                //step title
                it is TextView && (it.tag as String == STEP_TITLE_TAG) -> {
                    val centerPadding = (stepWidth - it.measuredWidth) / 2
                    it.layout(
                        left + centerPadding, top,
                        left + it.measuredWidth + centerPadding, top + it.measuredHeight
                    )
                }
                //step
                it is TextView && (it.tag as String != STEP_TITLE_TAG) -> {
                    val centerPadding = (stepWidth - it.measuredWidth) / 2
                    it.layout(
                        left + it.marginLeft + centerPadding,
                        top + it.marginTop,
                        left + it.measuredWidth + centerPadding,
                        top + it.measuredHeight + it.marginTop
                    )
                    left += it.measuredWidth + it.marginRight + it.marginLeft
                }
                //arc
                else -> {
                    val arcTop = ((b - t) - it.measuredHeight) / 2
                    it.layout(
                        left + it.marginStart,
                        arcTop + titleTextMaxHeight / 2,
                        left + it.measuredWidth - it.marginEnd,
                        arcTop + it.measuredHeight + titleTextMaxHeight / 2
                    )
                    left += it.measuredWidth
                }
            }
        }
    }

    private fun textViewForStepTitle(stepPosition: Int): TextView {
        return TextView(context).apply {
            text = beansList[stepPosition].title
            gravity = Gravity.TOP or Gravity.CENTER
            layoutParams = getDefaultElementLayoutParams()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textNodeTitleSize.toFloat())
            setTextColor(textNodeTitleColor)
            tag = STEP_TITLE_TAG
        }
    }

    private fun textViewForStep(stepPosition: Int, isActive: Boolean): TextView {
        return TextView(context).apply {
            background = if (isActive) currentDrawable else undoneDrawable
            gravity = Gravity.CENTER
            layoutParams = getDefaultElementLayoutParams()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textNodeSize.toFloat())
            tag = NODE_TAG_PREFIX + stepPosition
        }
    }


    private fun getDefaultElementLayoutParams(
        width: Int = LayoutParams.WRAP_CONTENT,
        height: Int = LayoutParams.WRAP_CONTENT
    ) = LinearLayout.LayoutParams(width, height)

    private fun arcView(position: Int): View {
        return View(context).apply {
            background = TransitionDrawable(arrayOf(arcInactiveDrawable, arcActiveDrawable))
            layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            tag = ARC_TAG_PREFIX + position
        }
    }

    private fun changeStepStateView(stepNumber: Int, newState: StepState) {
        findViewWithTag<TextView>(NODE_TAG_PREFIX + stepNumber)?.let {
            it.background = when (newState) {
                StepState.DONE -> doneDrawable
                StepState.DONE_WARNING -> doneWarningDrawable
                StepState.CURRENT -> currentDrawable
                else -> undoneDrawable
            }
        }
    }

    private fun animateProgressArc(arcNumber: Int, activated: Boolean) {
        findViewWithTag<View>(ARC_TAG_PREFIX + arcNumber)?.let {
            if (activated) {
                (it.background as TransitionDrawable).startTransition(arcTransitionDuration)
            } else {
                (it.background as TransitionDrawable).resetTransition()
            }
        }
    }

    fun setStepBean(beans: List<Bean>) {
        this.beansList = beans
        createViews()
        beans.forEachIndexed { index, bean ->
            if (bean.state == StepState.CURRENT) {
                currentStep = index
            }
            changeStepStateView(index, bean.state)
            if (index < beansList.lastIndex - 1) {
                animateProgressArc(index, isAllPreviousDone(index))
            }
        }
    }

    private fun isAllPreviousDone(index: Int): Boolean {
        for (i in 0..index + 1) {
            val state = beansList[i].state
            if (!(state == StepState.CURRENT || state == StepState.DONE)) return false
        }
        return true
    }


    private fun toPx(dp: Int, context: Context) =
        dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

    private fun toPx(dp: Float, context: Context) =
        dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

    private fun createStrokeOvalDrawable(context: Context, color: Int): Drawable {
        val strokeWidth = toPx(2, context)
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setStroke(strokeWidth, color)
        }
    }

    private fun createCheckDrawable(context: Context, color: Int): Drawable {
        val checkDrawable = ContextCompat.getDrawable(context, R.drawable.ic_check)
        return LayerDrawable(arrayOf(createOvalDrawable(color), checkDrawable)).apply {
            val ins = toPx(5, context)
            setLayerInset(1, ins, ins, ins, ins)
        }
    }

    private fun createOvalDrawable(color: Int): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    companion object {

        private const val NODE_TAG_PREFIX = "stn_"

        private const val ARC_TAG_PREFIX = "atn_"

        private const val STEP_TITLE_TAG = "step_title"

    }


}

data class Bean(val title: String, var state: StepState) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        StepState.fromOrdinal(parcel.readInt())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(state.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bean> {
        override fun createFromParcel(parcel: Parcel): Bean {
            return Bean(parcel)
        }

        override fun newArray(size: Int): Array<Bean?> {
            return arrayOfNulls(size)
        }
    }
}

enum class StepState {

    UNDONE, DONE, CURRENT, DONE_WARNING;

    companion object {

        fun fromOrdinal(ordinal: Int) = values().firstOrNull { it.ordinal == ordinal } ?: UNDONE
    }
}