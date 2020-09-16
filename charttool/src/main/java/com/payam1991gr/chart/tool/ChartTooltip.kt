package com.payam1991gr.chart.tool

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.util.DisplayUtils
import java.util.ArrayList

class ChartTooltip : ViewGroup {
    private val tooltipData = ArrayList<Int?>()
    private var partIndex: Int = -1
    private var descriptionWidget: CardView? = null
    private var titleView: AppCompatTextView? = null
    private var shadeView: View? = null
    private var holder: ICTWidgetParent? = null
    private var isAppearing = false
    private var isDisappearing = false
    private val legendTextList = ArrayList<AppCompatTextView>()
    private val valueTextList = ArrayList<AppCompatTextView>()
    private var onMeasureDemand = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        var maxHeight = 0
        var maxWidth = 0
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until count) {
            val child = getChildAt(i)
            val childRight: Int = child.x.toInt() + child.measuredWidth
            val childBottom: Int = child.y.toInt() + child.measuredHeight
            maxWidth = maxWidth.coerceAtLeast(childRight)
            maxHeight = maxHeight.coerceAtLeast(childBottom)
        }
        maxHeight = maxHeight.coerceAtLeast(suggestedMinimumHeight)
        maxWidth = maxWidth.coerceAtLeast(suggestedMinimumWidth)
        setMeasuredDimension(
            View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
            View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        plotView()
        updateDescriptionData()
    }

    fun setToolTipData(holder: ICTWidgetParent, tooltipData: List<Int?>) {
        this.holder = holder
        this.tooltipData.clear()
        this.tooltipData.addAll(tooltipData)
        makeDescriptionWidget()
        makeShadeView()
    }

    private fun makeDescriptionWidget() {
        if (descriptionWidget == null) {
            descriptionWidget = CardView(context).apply {
                layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                cardElevation = DisplayUtils.convertDpToPixel(0).toFloat()
                radius = DisplayUtils.convertDpToPixel(8).toFloat()

                val content = LinearLayout(context).apply {
                    DisplayUtils.convertDpToPixel(4).let { setPadding(it, it, it, it) }
                    orientation = LinearLayout.VERTICAL
                    setBackgroundResource(R.drawable.rounded_white)

                    titleView = AppCompatTextView(context).apply {
                        layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                            DisplayUtils.convertDpToPixel(4).let { setMargins(it, 0, it, 0) }
                        }
//                        holder?.getFontSize().let { size ->
//                            if (size == null)
//                                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                            else
//                                setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
//                        }
                        holder?.getTypeface().let { tf ->
                            if (tf == null)
                                setTypeface(typeface, Typeface.BOLD)
                            else
                                setTypeface(tf, Typeface.BOLD)
                        }
                        setTextColor(Color.BLACK)
                    }
                    addView(titleView)

                    for (i in 0 until (holder?.seriesCount() ?: 0))
                        addView(makeRow(holder?.dataAt(i)))
                }
                addView(content)
            }
            val direction = if (holder?.getRtl() == true)
                ViewCompat.LAYOUT_DIRECTION_RTL
            else
                ViewCompat.LAYOUT_DIRECTION_LTR
            ViewCompat.setLayoutDirection(descriptionWidget!!, direction)
            addView(descriptionWidget)
        }
    }

    private fun makeRow(data: CTData?): View = LinearLayout(context).apply {
        layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        orientation = LinearLayout.HORIZONTAL
        DisplayUtils.convertDpToPixel(4).let { setPadding(it, 0, it, 0) }
        gravity = Gravity.CENTER_VERTICAL

        val v = View(context).apply view@{
            val size = DisplayUtils.convertDpToPixel(8)
            layoutParams = LayoutParams(size, size)
            setBackgroundResource(R.drawable.circle)
            data?.apply { ViewCompat.setBackgroundTintList(this@view, ColorStateList.valueOf(color.toColor())) }
        }
        addView(v)

        val tv = AppCompatTextView(context).apply {
            layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                DisplayUtils.convertDpToPixel(4).let { setMargins(it, 0, it, 0) }
            }
//            text = context.getString(R.string.label_text).format(data?.name)
//            holder?.getFontSize().let { size ->
//                if (size == null)
//                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                else
//                    setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
//            }
            holder?.getTypeface().let { tf ->
                if (tf == null)
                    setTypeface(typeface, Typeface.BOLD)
                else
                    setTypeface(tf, Typeface.BOLD)
            }
            setTextColor(Color.BLACK)
        }
        legendTextList.add(tv)
        addView(tv)

        val tv2 = AppCompatTextView(context).apply {
            layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR)
//            text = data?.labels?.getOrNull(partIndex)
//            holder?.getFontSize().let { size ->
//                if (size == null)
//                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                else
//                    setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
//            }
            holder?.getTypeface().let { tf ->
                if (tf == null)
                    setTypeface(typeface, Typeface.BOLD)
                else
                    setTypeface(tf, Typeface.BOLD)
            }
            setTextColor(Color.BLACK)
        }
        valueTextList.add(tv2)
        addView(tv2)
    }

    private fun makeShadeView() {
        if (shadeView == null) {
            shadeView = View(context).apply {
                setBackgroundColor(Color.parseColor("#60cccccc"))
            }
            addView(shadeView)
        }
    }

    fun appearAt(partIndex: Int) {
        if (this.partIndex != partIndex) {
            this.partIndex = partIndex
            makeShadeView()
            makeDescriptionWidget()
            requestLayout()
            if (plotView()) {
                if (isAppearing)
                    return
                isAppearing = true
                alpha = 0f
                animate().alpha(1f).setDuration(300L).setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isAppearing = false
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        isAppearing = false
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                    .start()
            }
        }
    }

    private fun updateDescriptionData() {
        if (onMeasureDemand) {
            onMeasureDemand = false
            return
        }
        holder?.categoryAt(partIndex)?.let { titleView?.text = it }
        for (i in 0 until (holder?.seriesCount() ?: -1)) {
            holder?.dataAt(i)?.let { data ->
                data.name.let { legendTextList[i].text = context.getString(R.string.label_text).format(it) }
                data.labels.getOrNull(partIndex)?.let { valueTextList[i].text = it }
            }
        }
        onMeasureDemand = true
        titleView?.requestLayout()
    }

    fun disappear() {
        if (isDisappearing)
            return
        isDisappearing = true
        partIndex = -1
        animate().alpha(0f).setDuration(300L).setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isDisappearing = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isDisappearing = false
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
            .start()
    }

    private fun plotView(): Boolean {
        if (childCount == 0 || tooltipData.isEmpty())
            return false
        plotShade()
        plotDescription()
        return true
    }

    private fun plotDescription() {
        descriptionWidget?.let { descriptionWidget ->
            tooltipData.getOrNull(partIndex)?.let { y ->
                val w = descriptionWidget.measuredWidth
                val h = descriptionWidget.measuredHeight
                val hh = h / 2

                val part = measuredWidth / tooltipData.size

                var left = (partIndex + 1) * part
                var right = left + w
                if (left < 0) {
                    right -= left
                    left = 0
                } else if (right > measuredWidth) {
                    val shift = w + part
                    left -= shift
                    right -= shift
                }

                var top = y - hh
                var bottom = y + hh
                if (top < 0) {
                    bottom -= top
                    top = 0
                } else if (bottom > measuredHeight) {
                    top -= (bottom - measuredHeight)
                    bottom = measuredHeight
                }
                descriptionWidget.layout(left, top, right, bottom)
            }
        }
    }

    private fun plotShade() {
        val part = measuredWidth / tooltipData.size
        val start = partIndex * part
        shadeView?.layout(start, 0, start + part, measuredHeight)
    }
}
