package com.payam1991gr.chart.tool

import android.content.Context
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.TextureView
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.data.CT_Unit
import com.payam1991gr.chart.tool.renderer.TextureViewRenderer
import com.payam1991gr.chart.tool.util.DisplayUtils
import com.payam1991gr.chart.tool.util.getRawResString
import com.payam1991gr.chart.tool.util.plog
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.math.sin

// todo: put all the input data in a settings object and after show use them all
// todo: add color string parser
// todo: labels should accept vararg, array and ResInts
// todo: categories should accept vararg, array and ResInts
// todo: typeface must be either general or section specific
// todo: trim data deficiency
// todo: make lib customizable: yAxisGridLineWidth, legendEnabled, dataLabelsEnabled, ...
// todo: refactoring

// todo: make public classes as protocols or interfaces
// todo: workflow is ambiguous

// todo: add animation
// todo: proguard keep also keeps comments and private data
@Suppress("MemberVisibilityCanBePrivate")
open class ChartView2 : TextureView, TextureView.SurfaceTextureListener, IRendererParent,
    ICTWidgetParent {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private fun io(block: suspend CoroutineScope.() -> Unit) = scope.launch { block() }
    private fun ui(block: suspend CoroutineScope.() -> Unit) = mainScope.launch {
        try {
            block()
        } catch (e: Exception) {
        }
    }

    companion object {
        //        private const val ROTATION_TOUCH_SCALE_FACTOR: Float = 0.0625f
        private const val ANIMATION_DURATION = 150L
//        private const val ANIMATION_DURATION = 250L
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && background != null) {
            setBackgroundDrawable(background)
        }
    }

    private var dataList = ArrayList<CTData>()

    //    private val renderer: ChartRenderer
    private var renderer: TextureViewRenderer? = null
    private var animating = false
    private var legendView: ChartLegend? = null
    private var categoryView: ChartCategory? = null
    private var labelView: ChartLabel? = null
    private var tooltip: ChartTooltip? = null
    private var rtl = false
    private var typeface: Typeface? = null
    private var fontSize: Int? = null
    private val categories = ArrayList<String>()
    private var columnCount = 0
//    private var isPointerDown = false
//    private var waitingToHide = false

    init {
        surfaceTextureListener = this
//        setEGLContextClientVersion(2)
//        renderer = ChartRenderer(this)
//        setRenderer(renderer)
//        renderMode = RENDERMODE_WHEN_DIRTY
        checkDimensions()
    }

    private var prevW = -1
    private var prevH = -1
    private fun checkDimensions() {
        plog()
        viewTreeObserver.addOnGlobalLayoutListener {
            io {
                while (true) {
                    val w = width
                    val h = height
                    if (w != -1 && (prevW == -1 || w != prevW || h != prevH)) {
                        prevW = w
                        prevH = h
                        renderer?.onSizeChanged(w, h)
                    }
                    delay(10)
                }
            }
        }
    }

    // called every time when swapBuffers is called
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
//        plog()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        plog("width", width, "height", height)
//        renderer?.onSizeChanged(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
//        plog()
        renderer?.isStopped = true
        return false // surface.release() manually, after the last render
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        plog("width", width, "height", height)
        renderer = TextureViewRenderer(this, surface, width, height)
        renderer?.start()
    }

    fun data(vararg data: CTData): ChartView2 {
        this.dataList.apply {
            clear()
            addAll(data)
        }
        return this
    }

    fun categories(list: List<String>): ChartView2 {
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
        Handler().postDelayed({
            if (height > 0)
                startPlot()
            else
                getHeightLazy()
        }, 50L)
    }

    private fun startPlot() {
        plog()
        categoryView?.setCategories(this, categories)
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

    private fun drawBars() = io {
        try {
            renderer?.consumeData(dataList)
            // todo: decrease this delay
//                    delay(250L)
            animateChart()
        } catch (e: Exception) {
            plog("Error", e.message ?: "")
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
            try {
                renderer?.updateBaseLineSize(min, max)
                renderer?.count = count
            } catch (e: Exception) {
                plog("Error", e.message ?: "")
            }
        }
    }

    private fun animateChart() = io {
        plog()
        if (!animating) {
            animating = true
            legendView?.disappear()
            categoryView?.disappear()
            labelView?.disappear()
            legendView?.appear()
            categoryView?.appear()
            try {
                val timeStep = 1L // as fast as you can!
//            val timeStep = 17L // 60 fps
                var time = 0L
                while (time < ANIMATION_DURATION) {
                    delay(timeStep)
                    var ratio = time / ANIMATION_DURATION.toFloat()
                    plog("ratio", ratio, "time", time)
                    try {
                        // (1 - ratio).let { ratio -> sqrt(1 - ratio * ratio) }
                        // sqrt(ratio)
                        ratio = ((1 - sin(Math.PI * (ratio + 0.5f)).toFloat()) / 2f)
                        renderer?.animate(ratio)
                        renderer?.refresh()
                    } catch (e: Exception) {
                        plog("Error", e.message ?: "?")
                    }
                    time += timeStep
                }
                renderer?.animate(1f)
                renderer?.refresh()
                animating = false
                labelView?.appear()
            } catch (e: Exception) {
                plog("Error", e.message ?: "")
            }
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

    fun <T> radius(r: T, unit: CT_Unit = CT_Unit.DP): ChartView2 {
        try {
            renderer?.radius(r, unit)
        } catch (e: Exception) {
            plog("Error", e.message ?: "")
        }
        return this
    }

    fun highQuality(high: Boolean = true): ChartView2 {
        try {
            renderer?.highQuality = high
        } catch (e: Exception) {
            plog("Error", e.message ?: "")
        }
        return this
    }

    fun rtl(rtl: Boolean = true): ChartView2 {
        try {
            renderer?.rtl = true
            this.rtl = rtl
        } catch (e: Exception) {
            plog("Error", e.message ?: "")
        }
        return this
    }

    fun font(font: String, context: Context): ChartView2 {
        typeface = Typeface.createFromAsset(context.assets, font)
        return this
    }

    fun <T> fontSize(sp: T): ChartView2 {
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
                tooltipAppearanceJob?.cancel()
                tooltipAppearanceJob = null
                tooltip?.appearAt(positionToIndex(x))
            }
            MotionEvent.ACTION_MOVE -> tooltip?.appearAt(positionToIndex(x))
            MotionEvent.ACTION_UP -> lazyHideTooltip()
        }
        return true
    }

    private var tooltipAppearanceJob: Job? = null
    private fun lazyHideTooltip() = io {
        tooltipAppearanceJob = launch {
            delay(3000L)
            ui { tooltip?.disappear() }
        }
    }

    override fun setToolTipData(tooltipData: List<Int?>) {
        ui {
            try {
                tooltip?.setToolTipData(this@ChartView2, tooltipData)
            } catch (e: Exception) {
                plog("Error", e.message ?: "")
            }
        }
    }

    override fun dataAt(index: Int): CTData? = dataList.getOrNull(index)
    override fun categoryAt(index: Int): String? = categories.getOrNull(index)
    override fun seriesCount(): Int = dataList.size
}
