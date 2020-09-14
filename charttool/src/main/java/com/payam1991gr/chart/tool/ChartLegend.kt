package com.payam1991gr.chart.tool

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.payam1991gr.chart.tool.data.ICTWidgetParent
import com.payam1991gr.chart.tool.util.DisplayUtils
import com.payam1991gr.chart.tool.util.plog
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChartLegend : ViewGroup {
    companion object {
        private const val CIRCLE_SIZE = 16 // dp
    }

    private var legends = ArrayList<String>()
    private var legendColors = ArrayList<Int>()
    private var holder: ICTWidgetParent? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setLegends(holder: ICTWidgetParent, list: List<String>, colors: List<Int>) {
        this.holder = holder
        legends.clear()
        legends.addAll(list)
        legendColors.clear()
        legendColors.addAll(colors)
        buildChildren()
    }

    private fun buildChildren() {
        removeAllViews()
        val circleSize = DisplayUtils.convertDpToPixel(CIRCLE_SIZE)
        legends.forEachIndexed { index, legend ->
            val tv = AppCompatTextView(context).apply {
                text = legend
                holder?.getFontSize().let { size ->
                    if (size == null)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    else
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
                }
                holder?.getTypeface().let { tf ->
                    if (tf == null)
                        setTypeface(typeface, Typeface.BOLD)
                    else
                        setTypeface(tf, Typeface.BOLD)
                }
            }
            val v = View(context).apply {
                layoutParams = LayoutParams(circleSize, circleSize)
                setBackgroundResource(R.drawable.circle)
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(legendColors[index]))
            }
            addView(tv)
            addView(v)
        }
        alpha = 0f
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // #################################### < Weighted Space Distribution > ####################################
        val rtl = holder?.getRtl() == true
        val edgeWeight = 1.5f

        val count = childCount
        var contentWidth = 0
        for (i in 0 until count) {
            contentWidth += getChildAt(i).measuredWidth
        }
        val quota = 2 * edgeWeight + (count / 2) - 1

        val totalFreeSpace = measuredWidth - contentWidth
        // todo: check if totalFreeSpace > 0
        val innerSpace = (totalFreeSpace / quota).toInt()
        val edgeSpace = (edgeWeight * innerSpace).toInt()

        var startX = edgeSpace
        val centerY = measuredHeight / 2
        val circleSize = DisplayUtils.convertDpToPixel(CIRCLE_SIZE)
        val hCircleSize = circleSize / 2
        val circleMargin = DisplayUtils.convertDpToPixel(8)
        val range = if (rtl)
            (count - 2) downTo 0 step 2
        else
            0 until count step 2
        for (i in range) {
            val child = getChildAt(i)
            val colorView = getChildAt(i + 1)
            val w = child.measuredWidth
            val h = child.measuredHeight
            val hh = h / 2
//            plog("left", startX, "top", centerY - hh, "right", startX + w, "bottom", centerY + hh)
            child.layout(startX, centerY - hh, startX + w, centerY + hh)

            if (rtl)
                colorView.layout(startX + w + circleMargin, centerY - hCircleSize, startX + w + circleSize + circleMargin, centerY + hCircleSize)
            else
                colorView.layout(startX - circleSize - circleMargin, centerY - hCircleSize, startX - circleMargin, centerY + hCircleSize)
            startX += w + innerSpace
        }
        // #################################### </ Weighted Space Distribution > ####################################
        // #################################### < All The Same > ####################################
//        val count = childCount
//        val centerY = measuredHeight / 2
//        val circleSize = DisplayUtils.convertDpToPixel(CIRCLE_SIZE)
//        val circleMargin = DisplayUtils.convertDpToPixel(4)
//        val part = measuredWidth / 3
//        val hPart = part / 2
//        for (i in 0 until count step 2) {
//            val child = getChildAt(i)
//            val colorView = getChildAt(i + 1)
//            val w = child.measuredWidth
//            val hw = w / 2
//            val h = child.measuredHeight
//            val hh = h / 2
//            val centerX = hPart + i * part / 2
//            child.layout(centerX - hw, centerY - hh, centerX + hw, centerY + hh)
//            colorView.layout(centerX - hw - circleSize - circleMargin, centerY - hh, centerX - hw - circleMargin, centerY + hh)
//        }
        // #################################### </ All The Same > ####################################
        // #################################### < More Content, More Space > ####################################
//        val count = childCount
//        var contentWidth = 0
//        for (i in 0 until count step 2) {
//            contentWidth += getChildAt(i).measuredWidth
//        }
//        var currentX = 0
//        val centerY = measuredHeight / 2
//        val circleSize = DisplayUtils.convertDpToPixel(CIRCLE_SIZE)
//        val circleMargin = DisplayUtils.convertDpToPixel(4)
//        for (i in 0 until count step 2) {
//            val child = getChildAt(i)
//            val colorView = getChildAt(i + 1)
//            val w = child.measuredWidth
//            val hw = w / 2
//            val h = child.measuredHeight
//            val hh = h / 2
//            val part = w * measuredWidth / contentWidth
//            val centerX = part / 2 + currentX
//            child.layout(centerX - hw, centerY - hh, centerX + hw, centerY + hh)
//            colorView.layout(centerX - hw - circleSize - circleMargin, centerY - hh, centerX - hw - circleMargin, centerY + hh)
//            currentX += part
//        }
        // #################################### </ More Content, More Space > ####################################
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        var maxHeight = 0
        var maxWidth = 0
        measureChildren(widthMeasureSpec, heightMeasureSpec) // Find out how big everyone wants to be
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

    fun appear() {
        GlobalScope.launch(Main) {
            alpha = 0f
            animate().alpha(1f).setDuration(1000L).start()
        }
    }

    fun disappear() {
        GlobalScope.launch(Main) { alpha = 0f }
    }
}
