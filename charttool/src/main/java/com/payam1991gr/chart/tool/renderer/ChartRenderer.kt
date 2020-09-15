package com.payam1991gr.chart.tool.renderer

import android.annotation.TargetApi
import android.graphics.Outline
import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import com.payam1991gr.chart.tool.IRendererParent
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.data.CT_Unit
import com.payam1991gr.chart.tool.shape.Bar
import com.payam1991gr.chart.tool.shape.Rectangle
import com.payam1991gr.chart.tool.util.*
import java.lang.Exception
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ChartRenderer(private val parent: IRendererParent) : BaseRenderer(), GLSurfaceView.Renderer {
    private var displayRatio: Float = 1f
    private var invDisplayRatio: Float = 1f
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var baseLine: Rectangle? = null
    private var width: Int = 0
    private var height: Int = 0
    private var radiusUnit = CT_Unit.Native
    private var radius = .025f
        get() {
            return when (radiusUnit) {
                CT_Unit.Native -> field
                CT_Unit.Fraction -> field * 2f
                CT_Unit.PX -> convertPxToNative(field)
                CT_Unit.DP -> convertPxToNative(DisplayUtils.convertDpToPixel(field))
                CT_Unit.SP -> convertPxToNative(DisplayUtils.convertSpToPixel(field))
            }
        }

    private val barMap = ArrayList<ArrayList<Bar>>()
    private var dataList: List<CTData>? = null
    private var onNewData = false
    private var isReady = false
    var rtl = false

    @Volatile
    private var scale = 1f
    @Volatile
    private var min = 0
    @Volatile
    private var max = 0
    @Volatile
    private var positiveBarList = ArrayList<Bar>()
    @Volatile
    private var negativeBarList = ArrayList<Bar>()
    @Volatile
    var count = 0
        set(value) {
            step = 2f / value
            field = value
        }
    @Volatile
    private var step = 0f
    @Volatile
    private var base = 0f

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
//        plog("width", width, "height", height)
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
        displayRatio = width.toFloat() / height.toFloat()
        invDisplayRatio = 1 / displayRatio
        displayMinDim = if (width < height) width else height
        (invDisplayRatio / 2f).let { Matrix.frustumM(projectionMatrix, 0, .5f, -.5f, -it, it, it, invDisplayRatio * 2f) }
//        plog("invDisplayRatio", invDisplayRatio)

        baseLine = Rectangle(this)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -invDisplayRatio, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        if (isReady) {
            drawBaseLine(min, max)
            onNewData = true
            onDrawFrame(unused)
        }
        parent.onFrameChanged()
    }

    private fun makeLabelCoords() {
//        plog("width", width, "height", height, "invDisplayRatio", invDisplayRatio)
        val coords = ArrayList<Point?>()
        barMap.forEach { series ->
            val a = PointF(1f, invDisplayRatio)
            val b = width / 2f
            series.forEach {
                coords.add(((it.fixedCenter()?.apply { y = -y } + a) * b).toInt())
//                coords.add(((PointF() + a) * b)?.apply { y = height - y })
//                coords.add(((it.fixedCenter()?.apply { plog("fixedCenter.x", x, "fixedCenter.y", y) } + a) * b).toInt()?.apply { y = height - y })
            }
//            series.forEach { coords.add(((it.fixedCenter()?.apply { y = y } + a) * b).toInt()) }
        }
        parent.setLabelCoords(coords)
//            val coords = ArrayList<ArrayList<Point?>>()
//            barMap.forEach { series ->
//                val list = ArrayList<Point?>()
//                series.forEach { list.add((it.fixedCenter() * PointF(width, height)).toInt()) }
//                coords.add(list)
//            }
//            parent.setLabelCoords(coords)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        plog("isReady", isReady, "onNewData", onNewData)
        if (!isReady)
            return
//        plog()
        if (onNewData) {
            onNewData = false
            barMap.clear()
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
                            val bar = Bar(this@ChartRenderer).apply {
                                apply(PointF(xs, ys), PointF(xe, ye), radius, series.color)
                                fixPoints()
                            }
                            barMap[row].add(bar)
                            negativeBarList.add(bar)
                        } else {
                            val ys = base + positiveHeight
                            positiveHeight += scale * value
                            val ye = base + positiveHeight
                            val bar = Bar(this@ChartRenderer).apply {
                                apply(PointF(xs, ys), PointF(xe, ye), radius, series.color)
                                fixPoints()
                            }
                            barMap[row].add(bar)
                            positiveBarList.add(bar)
                        }
                    }
                }
                makeLabelCoords()
            }
        } else {
//        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -invDisplayRatio, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
//        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            try {
                positiveBarList.forEach { it.draw(vPMatrix) }
                negativeBarList.forEach { it.draw(vPMatrix) }
            } catch (e: Exception) {
                plog("Error", e.message ?: "?")
            }
            baseLine?.draw(vPMatrix)
        }
    }

    override fun getShaderCode(shaderRes: Int): String = parent.getShaderCode(shaderRes)

    //    fun drawBaseLine(height: Float, min: Int, max: Int) {
    fun drawBaseLine(min: Int, max: Int) {
//        plog()
        this.min = min
        this.max = max
//        val h = invDisplayRatio * (2 * height - 1)
        val h = -invDisplayRatio
        scale = 2 * invDisplayRatio / (max - min)
//        scale = (invDisplayRatio - h) / (max - min)
        base = h - scale * min
//        plog("invDisplayRatio", invDisplayRatio, "scale", scale, "base", base)
//        plog("base", base, "scale", scale, "h", h)
//        plog("displayMinDim", displayMinDim)
        val dy = invDisplayRatio / 200f
        baseLine?.apply(PointF(-1f, base - dy), PointF(1f, base + dy), GLColor.LightGray)
//        scale *= .95f
    }

    fun consumeData(dataList: List<CTData>) {
//        plog()
        this.dataList = dataList
        onNewData = true
        isReady = true
    }

    fun animate(ratio: Float) {
        positiveBarList.forEach { it.fixApply(ratio, base) }
        negativeBarList.forEach { it.fixApply(ratio, base) }
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

    override fun highQuality(): Boolean = highQuality
//    fun getCoordAt(x: Float, y: Float): PointF {
//        return PointF(x / width, y / height)
//    }
}
