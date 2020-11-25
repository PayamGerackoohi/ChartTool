package com.payam1991gr.chart.tool

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.payam1991gr.chart.tool.util.toDegrees
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.acos
import kotlin.math.sqrt

class ChartCategory : ViewGroup {
    private val categories = ArrayList<String>()
    private var holder: ICTWidgetParent? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return holder?.onTouchEvent(e) ?: false
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private fun io(block: suspend CoroutineScope.() -> Unit) = scope.launch { block() }

    private fun ui(block: suspend CoroutineScope.() -> Unit) = mainScope.launch {
        try {
            block()
        } catch (e: Exception) {
        }
    }

    fun setCategories(holder: ICTWidgetParent, list: List<String>) {
        this.holder = holder
        categories.clear()
        categories.addAll(list)
        buildCategories()
    }

    private fun buildCategories() {
        removeAllViews()
        categories.forEach { category ->
            val tv = AppCompatTextView(context).apply {
                text = context.getString(R.string.fa_template).format(category)
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
            addView(tv)
        }
        alpha = 0f
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0)
            return
        val count = childCount
        val part = measuredWidth / count
        val hPart = part / 2
        var startCenterX = hPart
        val centerY = measuredHeight / 2
        for (i in 0 until count) {
            val child = getChildAt(i)
            val endCenterX = startCenterX + part
            val hw = child.measuredWidth / 2
            val hh = child.measuredHeight / 2
            child.layout(startCenterX - hw, centerY - hh, startCenterX + hw, centerY + hh)
            startCenterX = endCenterX
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec) // Find out how big everyone wants to be
        if (childCount == 0) {
            setMeasuredDimension(
                View.resolveSizeAndState(0, widthMeasureSpec, 0),
                View.resolveSizeAndState(0, heightMeasureSpec, 0)
            )
        } else {
            var maxHeight = 0
            var maxWidth = 0
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                maxWidth = maxWidth.coerceAtLeast(child.measuredWidth)
                maxHeight = maxHeight.coerceAtLeast(child.measuredHeight)
            }
            val length = maxWidth.toFloat()
            val part = measuredWidth / childCount
            var angel = 0f
            var height = 0
            if (length > part) {
                angel = -acos(part / length).toDegrees()
                height = sqrt(length * length - part * part).toInt()
            }
            height = height.coerceAtLeast(maxHeight)
            setMeasuredDimension(
                View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                View.resolveSizeAndState(height, heightMeasureSpec, 0)
            )
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.rotation = angel
            }
        }
    }

    fun appear() {
        ui {
            alpha = 0f
            animate().alpha(1f).setDuration(1000L).start()
        }
    }

    fun disappear() {
        ui { alpha = 0f }
    }
}
