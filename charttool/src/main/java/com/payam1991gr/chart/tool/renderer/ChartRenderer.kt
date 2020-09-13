package com.payam1991gr.chart.tool.renderer

import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.payam1991gr.chart.tool.IRendererParent
import com.payam1991gr.chart.tool.shape.Bar
import com.payam1991gr.chart.tool.util.GLColor
import com.payam1991gr.chart.tool.util.plog
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ChartRenderer(private val parent: IRendererParent) : GLSurfaceView.Renderer, IShapeParent {
    companion object {
        var DisplayRatio: Float = 1f
        var InvDisplayRatio: Float = 1f
        var DisplayMinDim: Int = 0
        @Volatile
        var HighQuality: Boolean = false
    }

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private lateinit var bar1: Bar
    private val rotationMatrix = FloatArray(16)

    @Volatile
    private var scale = .3f
    @Volatile
    private var horScale = 1.0f
    @Volatile
    private var verScale = 1.0f
    @Volatile
    var angle = 0f

    private val testScale = .8f
    //    private val testScale = 1f
    //    private val start = PointF(-testScale, -testScale)
//    private val end = PointF(testScale, testScale)
    private val start = PointF(-testScale, -testScale * .3f)
    private val end = PointF(testScale, testScale * .3f)
    private val color = GLColor.Blue

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        DisplayRatio = width.toFloat() / height.toFloat()
        InvDisplayRatio = 1 / DisplayRatio
        DisplayMinDim = if (width < height) width else height
        (InvDisplayRatio / 2f).let { Matrix.frustumM(projectionMatrix, 0, .5f, -.5f, -it, it, it, InvDisplayRatio * 2f) }

        bar1 = Bar(this)
        bar1.apply(start, end, scale, color)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val matrix = FloatArray(16)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -InvDisplayRatio, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)
        Matrix.multiplyMM(matrix, 0, vPMatrix, 0, rotationMatrix, 0)
        bar1.draw(matrix)
    }

    fun setScale(diff: Float) {
        scale += diff
        if (scale < -0.1f)
            scale = -0.1f
        else if (scale > 1.1f)
            scale = 1.1f
        bar1.updateRadius(scale)
    }

    private val barWidth = (end.x - start.x)
    private val barHeight = (end.y - start.y)
    fun setHVScale(diffX: Float, diffY: Float) {
        horScale += diffX / barWidth * 2f * 2f
        if (horScale < -0.1f)
            horScale = -0.1f
//        else if (horScale > 2f)
//            horScale = 2f
        verScale += diffY / barHeight * 2f * 2f / DisplayRatio
        if (verScale < -0.1f)
            verScale = -0.1f
//        else if (verScale > 2f)
//            verScale = 2f
        val dx = (1f - horScale) * barWidth / 2f
        val dy = (1f - verScale) * barHeight / 2f
        bar1.apply(PointF(start.x + dx, start.y + dy), PointF(end.x - dx, end.y - dy), scale, color)
    }

    fun setHighQuality(highQuality: Boolean = false) {
        HighQuality = highQuality
        bar1.refresh()
    }

    override fun getShaderCode(shaderRes: Int): String = parent.getShaderCode(shaderRes)
}
