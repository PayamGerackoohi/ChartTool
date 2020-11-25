package com.payam1991gr.chart.tool.renderer

import android.graphics.Point
import android.graphics.PointF
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.payam1991gr.chart.tool.IRendererParent
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.data.CT_Unit
import com.payam1991gr.chart.tool.data.CT_Unit.*
import com.payam1991gr.chart.tool.shape.Bar
import com.payam1991gr.chart.tool.shape.Rectangle
import com.payam1991gr.chart.tool.util.*
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TextureViewRenderer(private val parent: IRendererParent, surface: SurfaceTexture, width: Int, height: Int) :
    BaseTextureViewRenderer(parent, surface, width, height) {
    enum class DrawState {
        OnNewData, ParsingData, Ready, None
    }

    private val animationLock = ReentrantLock(true)
    private var drawState = DrawState.None
    private var radiusUnit = Native
    private var radius = .025f
        get() {
            return when (radiusUnit) {
                Native -> field
                Fraction -> field * 2f
                PX -> convertPxToNative(field)
                DP -> convertPxToNative(DisplayUtils.convertDpToPixel(field))
                SP -> convertPxToNative(DisplayUtils.convertSpToPixel(field))
            }
        }

    private val barMap = ArrayList<ArrayList<Bar>>()

    var rtl = false
    private var dataList: List<CTData>? = null
    //    private var onNewData = false
//    private var isReady = false
    private var scale = 1f
    private var min = 0
    private var max = 0
    private var positiveBarList = ArrayList<Bar>()
    private var negativeBarList = ArrayList<Bar>()
    var count = 0
        set(value) {
            step = 2f / value
            field = value
        }
    private var step = 0f
    private var base = 0f

    private var baseLine: Rectangle? = null

    override fun onInit() {
        try {
            plog()
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
            baseLine = Rectangle(this)
        } catch (e: Exception) {
            plog("Error", e.message ?: "")
        }
    }

    override fun applyDimensions() {
        super.applyDimensions()

        if (drawState == DrawState.Ready) {
            updateBaseLineSize(min, max)
            drawState = DrawState.OnNewData
//            onDrawFrame(unused)
        }
        parent.onFrameChanged()
    }

    override fun onDraw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        when (drawState) {
            DrawState.OnNewData -> {
                drawState = DrawState.ParsingData
                barMap.clear()
                positiveBarList.clear()
                negativeBarList.clear()
                val tooltipData = ArrayList<Int?>()
                (0 until count).forEach { _ -> barMap.add(ArrayList()) }
                (0 until count).forEach { column ->
                    var positiveHeight = 0f
                    var negativeHeight = 0f
                    val stepMargin = .15f
                    val xs = (column + stepMargin) * step - 1
                    val xe = (column + 1 - stepMargin) * step - 1
                    dataList?.forEachIndexed { row, series ->
                        series.values.getOrNull(column)?.let { value ->
                            if (value < 0) {
                                val ye = base + negativeHeight
                                negativeHeight += scale * value
                                val ys = base + negativeHeight
                                val bar = Bar(this@TextureViewRenderer).apply {
                                    apply(PointF(xs, ys), PointF(xe, ye), radius, series.color)
                                    fixPoints()
                                    fixApply(0f, 0f)
                                }
                                barMap[row].add(bar)
                                negativeBarList.add(bar)
                            } else {
                                val ys = base + positiveHeight
                                positiveHeight += scale * value
                                val ye = base + positiveHeight
                                val bar = Bar(this@TextureViewRenderer).apply {
                                    apply(PointF(xs, ys), PointF(xe, ye), radius, series.color)
                                    fixPoints()
                                    fixApply(0f, 0f)
                                }
                                barMap[row].add(bar)
                                positiveBarList.add(bar)
                            }
                        }
                    }
                    tooltipData.add(nativeToAndroidCoordY(base + positiveHeight))
                }
                parent.setToolTipData(tooltipData)
                makeLabelCoords()
                drawState = DrawState.Ready
            }
            DrawState.Ready -> {
                positiveBarList.forEach { it.draw(vPMatrix) }
                negativeBarList.forEach { it.draw(vPMatrix) }
                baseLine?.draw(vPMatrix)
            }
            else -> {
            }
        }
    }

    private fun makeLabelCoords() {
        val coords = ArrayList<Point?>()
        barMap.forEach { series -> series.forEach { coords.add(nativeToAndroidCoord(it.fixedCenter())) } }
        parent.setLabelCoords(coords)
    }

    fun updateBaseLineSize(min: Int, max: Int) {
        this.min = min
        this.max = max
        val h = -invDisplayRatio
        scale = 2 * invDisplayRatio / (max - min)
        base = h - scale * min
        val dy = convertPxToNative(DisplayUtils.convertDpToPixel(1))
//        val dy = invDisplayRatio / 200f
        baseLine?.apply(PointF(-1f, base - dy), PointF(1f, base + dy), GLColor.LightGray)
    }

    fun consumeData(dataList: List<CTData>) {
        this.dataList = dataList
        drawState = DrawState.OnNewData
    }

    fun animate(ratio: Float) {
//        animationLock.withLock {
        positiveBarList.forEach { it.fixApply(ratio, base) }
        negativeBarList.forEach { it.fixApply(ratio, base) }
//        }
    }

    private fun <T> convertPxToNative(px: T): Float {
        return when (px) {
            is Int -> px.toFloat()
            is Long -> px.toFloat()
            is Float -> px
            is Double -> px.toFloat()
            else -> 0f
        } * 2 / width
    }

    fun <T> radius(r: T, unit: CT_Unit) {
        radius = when (r) {
            is Int -> r.toFloat()
            is Long -> r.toFloat()
            is Float -> r
            is Double -> r.toFloat()
            else -> 0f
        }
        radiusUnit = unit
    }

    private fun nativeToAndroidCoord(point: PointF?): Point? {
        val a = PointF(1f, invDisplayRatio)
        val b = width / 2f
        return ((point?.apply { y = -y } + a) * b).toInt()
    }

    private fun nativeToAndroidCoordY(y: Float?): Int? {
        return y?.let { ((invDisplayRatio - y) * width / 2f).toInt() }
    }
}
