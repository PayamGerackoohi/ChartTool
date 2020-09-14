package com.payam1991gr.chart.tool.renderer

import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.payam1991gr.chart.tool.IRendererParent
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.shape.Bar
import com.payam1991gr.chart.tool.shape.Line
import com.payam1991gr.chart.tool.util.GLColor
import com.payam1991gr.chart.tool.util.plog
import java.lang.Exception
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ChartRenderer(private val parent: IRendererParent) : BaseRenderer(), GLSurfaceView.Renderer {
    private var displayRatio: Float = 1f
    private var invDisplayRatio: Float = 1f
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private var baseLine: Line? = null
    private val radius = .025f

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

    private var dataList: List<CTData>? = null
    private var onNewData = false

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        displayRatio = width.toFloat() / height.toFloat()
        invDisplayRatio = 1 / displayRatio
        displayMinDim = if (width < height) width else height
        (invDisplayRatio / 2f).let { Matrix.frustumM(projectionMatrix, 0, .5f, -.5f, -it, it, it, invDisplayRatio * 2f) }
        plog("invDisplayRatio", invDisplayRatio)

        baseLine = Line(this)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -invDisplayRatio, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        if (onNewData) {
            onNewData = false
            (0 until count).forEach { index ->
                var positiveHeight = 0f
                var negativeHeight = 0f
                val stepMargin = .15f
                val xs = (index + stepMargin) * step - 1
                val xe = (index + 1 - stepMargin) * step - 1
//                val xe = xs + step * (1 - 2 * stepMargin)
                dataList?.forEach { data ->
                    data.values.getOrNull(index)?.let { value ->
                        if (value < 0) {
                            val ye = base + negativeHeight
                            negativeHeight += scale * value
                            val ys = base + negativeHeight
                            negativeBarList.add(Bar(this@ChartRenderer).apply {
                                apply(PointF(xs, ys), PointF(xe, ye), radius, data.color)
                                fixPoints()
                            })
                        } else {
                            val ys = base + positiveHeight
                            positiveHeight += scale * value
                            val ye = base + positiveHeight
                            positiveBarList.add(Bar(this@ChartRenderer).apply {
                                apply(PointF(xs, ys), PointF(xe, ye), radius, data.color)
                                fixPoints()
                            })
                        }
                    }
                }
            }
        } else {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
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

    fun drawBaseLine(height: Float, min: Int, max: Int) {
        this.min = min
        this.max = max
        val h = invDisplayRatio * (2 * height - 1)
        scale = (invDisplayRatio - h) / (max - min)
        base = h - scale * min
        plog("base", base, "scale", scale, "h", h)
        baseLine?.apply(PointF(-1f, base), PointF(1f, base), GLColor.Gray)
//        scale *= .95f
    }

    fun consumeData(dataList: List<CTData>) {
        this.dataList = dataList
        onNewData = true
//        var positiveHeight = 0f
//        var negativeHeight = 0f
//        (0 until count).forEach { index ->
//            val xs = index * step
//            val xe = (index + 1) * step
//            dataList.forEach { data ->
//                data.values.getOrNull(index)?.let { value ->
//                    if (value < 0) {
//                        val ye = base + negativeHeight
//                        negativeHeight += scale * value
//                        val ys = base + negativeHeight
//                        negativeBarList.add(Bar(this@ChartRenderer).apply { apply(PointF(xs, ys), PointF(xe, ye), radius, data.color) })
//                    } else {
//                        val ys = base + positiveHeight
//                        positiveHeight += scale * value
//                        val ye = base + positiveHeight
//                        positiveBarList.add(Bar(this@ChartRenderer).apply { apply(PointF(xs, ys), PointF(xe, ye), radius, data.color) })
//                    }
//                }
//            }
//        }
    }

    fun animate(ratio: Float) {
        positiveBarList.forEach { it.fixApply(ratio, base) }
        negativeBarList.forEach { it.fixApply(ratio, base) }
    }

//    fun setCount(count: Int) {
//        this.count = count
//    }
}
