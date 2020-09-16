package com.payam1991gr.chart.tool

import android.content.Context
import android.graphics.Point
import android.graphics.Typeface
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.data.CT_Unit
import com.payam1991gr.chart.tool.renderer.ChartRenderer
import com.payam1991gr.chart.tool.util.DisplayUtils
import com.payam1991gr.chart.tool.util.getRawResString
import com.payam1991gr.chart.tool.util.plog
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.sin

//todo: workflow is unambiguous
@Suppress("MemberVisibilityCanBePrivate")
class ChartView : GLSurfaceView, IRendererParent, ICTWidgetParent {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    companion object {
        //        private const val ROTATION_TOUCH_SCALE_FACTOR: Float = 0.0625f
        private const val ANIMATION_DURATION = 250L
    }

    private var dataList = ArrayList<CTData>()
    private val renderer: ChartRenderer
    private var animating = false
    private var legendView: ChartLegend? = null
    private var categoryView: ChartCategory? = null
    private var labelView: ChartLabel? = null
    private var tooltip: ChartTooltip? = null
    private var rtl = false
    private var typeface: Typeface? = null
    private var fontSize: Int? = null
    private val categories = ArrayList<String>()
    private var isPointerDown = false
    private var columnCount = 0
    private var waitingToHide = false

    init {
        setEGLContextClientVersion(2)
        renderer = ChartRenderer(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun data(vararg data: CTData): ChartView {
        this.dataList.apply {
            clear()
            addAll(data)
        }
        return this
    }

    fun categories(list: List<String>): ChartView {
        categories.clear()
        categories.addAll(list)
        return this
    }

    fun show() {
        if (height > 0)
            startPlot()
        else
            getHeightLazy()
    }

    private fun getHeightLazy() {
        categoryView?.setCategories(this, categories)
        Handler().postDelayed({
            if (height > 0)
                startPlot()
            else
                getHeightLazy()
        }, 50)
    }

    private fun startPlot() {
        gatherResources()
        drawBaseLine()
        // todo: drawValueBar()
        drawBars()
    }

    private fun gatherResources() {
        val legends = ArrayList<String>()
        val legendColors = ArrayList<Int>()
        val labels = ArrayList<ArrayList<String>>()
        dataList.forEach { data ->
            data.nameId?.let { data.name(context.getString(it)) }
            data.colorId?.let { data.color(ContextCompat.getColor(context, it)) }
            labels.addAll(listOf(data.labels))
            legends.add(data.name)
            legendColors.add(data.color.toColor())
        }
        columnCount = dataList.getOrNull(0)?.values?.size ?: 0
        legendView?.setLegends(this, legends, legendColors)
        labelView?.setLabels(this, labels)
    }

    private fun drawBars() {
        renderer.consumeData(dataList)
        GlobalScope.launch {
            // todo: decrease this delay
            Thread.sleep(750)
            animateChart()
        }
    }

    private fun drawBaseLine() {
        var count = 0
        dataList.forEach { data ->
            data.values.apply {
                if (count < size)
                    count = size
            }
        }
        var min = 0
        var max = 0
        (0 until count).forEach { index ->
            var localMin = 0
            var localMax = 0
            dataList.forEach { data ->
                data.values.getOrNull(index)?.let { value ->
                    if (value < 0) {
                        localMin += value
                        if (min > localMin)
                            min = localMin
                    } else {
                        localMax += value
                        if (max < localMax)
                            max = localMax
                    }

                }
            }
        }
        if (count == 0) {
            plog("No Data")
            // todo: show user
        } else {
            renderer.drawBaseLine(min, max)
            renderer.count = count
        }
    }

    fun animateChart() {
        if (animating)
            return
        animating = true
        legendView?.disappear()
        categoryView?.disappear()
        labelView?.disappear()
        legendView?.appear()
        categoryView?.appear()
        GlobalScope.launch {
            val timeStep = 1L // as fast as you can!
//            val timeStep = 17L // 60 fps
            var time = 0L
            while (time < ANIMATION_DURATION) {
                Thread.sleep(timeStep)
                var ratio = time / ANIMATION_DURATION.toFloat()
//                plog("ratio", ratio, "time", time)
                try {
                    // (1 - ratio).let { ratio -> sqrt(1 - ratio * ratio) }
                    // sqrt(ratio)
                    ratio = ((1 - sin(Math.PI * (ratio + 0.5f)).toFloat()) / 2f)
                    renderer.animate(ratio)
                    requestRender()
                } catch (e: Exception) {
                    plog("Error", e.message ?: "?")
                }
                time += timeStep
            }
            renderer.animate(1f)
            requestRender()
            animating = false
            labelView?.appear()
        }
    }

    fun setLegendView(legendView: ChartLegend) {
        this.legendView = legendView
    }

    fun setCategoryView(categoryView: ChartCategory) {
        this.categoryView = categoryView
    }

    fun setLabelView(label: ChartLabel) {
        this.labelView = label
    }

    fun setTooltipView(tooltip: ChartTooltip) {
        this.tooltip = tooltip
    }

    fun <T> radius(r: T, unit: CT_Unit = CT_Unit.DP): ChartView {
        renderer.radius(r, unit)
        return this
    }

    fun highQuality(high: Boolean = true): ChartView {
        renderer.highQuality = high
        return this
    }

    fun rtl(rtl: Boolean = true): ChartView {
        renderer.rtl = true
        this.rtl = rtl
        return this
    }

    fun font(font: String, context: Context): ChartView {
        typeface = Typeface.createFromAsset(context.assets, font)
        return this
    }

    fun <T> fontSize(sp: T): ChartView {
        when (sp) {
            is Int -> sp.toFloat()
            is Float -> sp
            else -> null
        }?.let { this.fontSize = DisplayUtils.convertSpToPixel(it) }
        return this
    }

    override fun onFrameChanged() {
        labelView?.refresh()
    }

    override fun setLabelCoords(coords: List<Point?>) {
        labelView?.updateCoords(coords)
    }

    override fun getShaderCode(@RawRes shaderRes: Int): String = context.getRawResString(shaderRes)
    override fun getRtl(): Boolean = rtl
    override fun getTypeface(): Typeface? = typeface
    override fun getFontSize(): Int? = fontSize

//    override fun performClick(): Boolean {
//        super.performClick()
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//        return true
//    }

    private fun positionToIndex(x: Float): Int {
        return (x * columnCount / width).toInt().let { if (it == columnCount) it - 1 else it }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
//        todo: implement performClick
        val x: Float = e.x

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                isPointerDown = true
                tooltip?.appearAt(positionToIndex(x))
            }
            MotionEvent.ACTION_MOVE -> tooltip?.appearAt(positionToIndex(x))
            MotionEvent.ACTION_UP -> {
                isPointerDown = false
                if (!waitingToHide) {
                    waitingToHide = true
                    GlobalScope.launch {
                        Thread.sleep(3000L)
                        waitingToHide = false
                        if (!isPointerDown) {
                            GlobalScope.launch(Main) { tooltip?.disappear() }
                        }
                    }
                }
            }
        }
        return true
    }

    override fun setToolTipData(tooltipData: List<Int?>) {
        GlobalScope.launch(Main) { tooltip?.setToolTipData(this@ChartView, tooltipData) }
    }

    override fun dataAt(index: Int): CTData? = dataList.getOrNull(index)
    override fun categoryAt(index: Int): String? = categories.getOrNull(index)
    override fun seriesCount(): Int = dataList.size
}
