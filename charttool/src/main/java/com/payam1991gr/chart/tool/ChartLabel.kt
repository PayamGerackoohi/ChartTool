package com.payam1991gr.chart.tool

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Point
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class ChartLabel : ViewGroup {
    private var holder: ICTWidgetParent? = null
    private val coords = ArrayList<Point?>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setLabels(holder: ICTWidgetParent, list: ArrayList<ArrayList<String>>) {
        plog()
        this.holder = holder
        buildLabels(list)
    }

    private fun buildLabels(labels: ArrayList<ArrayList<String>>) {
        removeAllViews()
        labels.forEach { series ->
            series.forEach { label ->
                val tv = AppCompatTextView(context).apply {
                    text = label
                    holder?.getFontSize().let { size ->
                        if (size == null)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        else
                            setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
                    }
                    holder?.getTypeface().let { tf ->
                        if (tf == null)
                            setTypeface(typeface, Typeface.BOLD)
                        else
                            setTypeface(tf, Typeface.BOLD)
                    }
                    val padding = DisplayUtils.convertDpToPixel(8)
                    setPadding(padding, padding, padding, padding)
                    setShadowLayer(6f, 0f, 0f, Color.WHITE)
                    // todo: shadow strength
//                    setShadowLayer(.02f, 0f, 0f, Color.WHITE)
//                    setShadowLayer(.02f, 4f, 4f, Color.WHITE)
                    setTextColor(Color.BLACK)
                }
                ViewCompat.setLayoutDirection(tv, ViewCompat.LAYOUT_DIRECTION_LTR)
                addView(tv)
            }
        }
        alpha = 0f
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        plog()
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
        GlobalScope.launch(Dispatchers.Main) {
            alpha = 0f
            animate().alpha(1f).setDuration(1000L).start()
        }
    }

    fun disappear() {
        GlobalScope.launch(Dispatchers.Main) { alpha = 0f }
    }

    fun updateCoords(coords: List<Point?>) {
        plog()
        this.coords.clear()
        this.coords.addAll(coords)
        refresh()
    }

    fun refresh() {
//        plog("measuredWidth", measuredWidth, "measuredHeight", measuredHeight)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val coord = coords.getOrNull(i)
            val w = child.measuredWidth
            val h = child.measuredHeight
            val hw = w / 2
            val hh = h / 2
            coord?.apply {
                //                plog("w", w, "h", h, "coord.x", it.x, "coord.y", it.y)
                child.layout(x - hw, y - hh, x + hw, y + hh)
                invalidate()
            }
        }
    }
}
