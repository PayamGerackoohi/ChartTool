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
import com.payam1991gr.chart.tool.data.ICTWidgetParent
import com.payam1991gr.chart.tool.renderer.ChartRenderer
import com.payam1991gr.chart.tool.util.DisplayUtils
import com.payam1991gr.chart.tool.util.getRawResString
import com.payam1991gr.chart.tool.util.plog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ChartView : GLSurfaceView, IRendererParent, ICTWidgetParent {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    companion object {
        //        private const val ROTATION_TOUCH_SCALE_FACTOR: Float = 0.0625f
//        private const val ANIMATION_DURATION = 500L
        private const val ANIMATION_DURATION = 200L
//        private const val ANIMATION_DURATION = 250L
    }
    //    private var previousX: Float = 0f
//    private var previousY: Float = 0f
//    private var cornerRadius = false

    private var dataList = ArrayList<CTData>()
    private val renderer: ChartRenderer
    private var animating = false
    private var legendView: ChartLegend? = null
    private var categoryView: ChartCategory? = null
    private var labelView: ChartLabel? = null
    private var rtl = false
    private var typeface: Typeface? = null
    private var fontSize: Int? = null
    private val categories = ArrayList<String>()

    init {
        setEGLContextClientVersion(2)
        renderer = ChartRenderer(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        setOnClickListener { animateChart() }
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
        legendView?.setLegends(this, legends, legendColors)
        labelView?.setLabels(this, labels)
    }

    private fun drawBars() {
        renderer.consumeData(dataList)
//        requestRender()
        GlobalScope.launch {
            Thread.sleep(500)
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
//            renderer.drawBaseLine(DisplayUtils.convertDpToPixel(2 * 16) / height.toFloat(), min, max)
            renderer.count = count
//            requestRender()
        }
    }

    private fun animateChart() {
        plog("animating", animating)
        if (animating)
            return
        animating = true
        legendView?.disappear()
        categoryView?.disappear()
        labelView?.disappear()
        legendView?.appear()
        categoryView?.appear()
        GlobalScope.launch {
            val timeStep = 1L
//            val timeStep = 30L
            var time = 0L
            while (time < ANIMATION_DURATION) {
                Thread.sleep(timeStep)
                val ratio = time / ANIMATION_DURATION.toFloat()
//                plog("ratio", ratio, "time", time)
                try {
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
//        plog()
        labelView?.refresh()
    }

    override fun setLabelCoords(coords: List<Point?>) {
//        plog("width", width, "height", height)
        labelView?.updateCoords(coords)
    }

    override fun getShaderCode(@RawRes shaderRes: Int): String = context.getRawResString(shaderRes)
    override fun getRtl(): Boolean = rtl
    override fun getTypeface(): Typeface? = typeface
    override fun getFontSize(): Int? = fontSize
//    override fun performClick(): Boolean {
//        super.performClick()
//        notifyUser("Chart Changed!")
//        return true
//    }
//
//    @Suppress("SameParameterValue")
//    private fun notifyUser(message: String) {
////        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
//        todo: implement performClick
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
//                var dx: Float = x - previousX
//                var dy: Float = y - previousY
//                if (cornerRadius) {
//                    renderer.setScale(-dy / height)
//                } else {
//                    if (rotate) {
//                        if (y > height / 2)
//                            dx *= -1
//                        if (x < width / 2)
//                            dy *= -1
//                        renderer.angle += (dx + dy) * ROTATION_TOUCH_SCALE_FACTOR
//                    } else {
//                        renderer.setHVScale(dx / width, -dy / height)
//                    }
//                }
//                requestRender()
            }
            MotionEvent.ACTION_UP -> {
//                val coord = renderer.getCoordAt(x, y)
//                plog("x", x, "y", y, "coord.x", coord.x, "coord.y", coord.y)
            }
        }
//            MotionEvent.ACTION_UP -> performClick()
//        previousX = x
//        previousY = y
//        return true
        return super.onTouchEvent(e)
    }
}
