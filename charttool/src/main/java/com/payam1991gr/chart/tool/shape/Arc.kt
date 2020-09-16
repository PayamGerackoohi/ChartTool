package com.payam1991gr.chart.tool.shape

import android.graphics.PointF
import android.opengl.GLES20
import com.payam1991gr.chart.tool.renderer.BaseRenderer
import com.payam1991gr.chart.tool.renderer.ChartRenderer
import com.payam1991gr.chart.tool.renderer.IShapeParent
import com.payam1991gr.chart.tool.util.GLColor
import com.payam1991gr.chart.tool.util.plog
import com.payam1991gr.chart.tool.util.toRadians
import java.nio.FloatBuffer
import kotlin.math.*

// todo: needs optimization
class Arc(private val parent: IShapeParent) : BaseShape(parent) {
    companion object {
        private const val COORDS_PER_VERTEX = 3
        private const val VertexStride = COORDS_PER_VERTEX * 4
    }

    private var center = PointF()
    private var radius = 0f
    private var startDegree = 0f
    private var endDegree = 0f
    private var color = GLColor.Black
    private var skewness: Float = 1f
    private var precisionFactor: Float = 1f

    private var vertexCount: Int = 3
    private var vertexBuffer: FloatBuffer? = null

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var vPMatrixHandle: Int = 0

    private var valid = false

    init {
        setupShaders()
    }

    fun apply(
        center: PointF,
        radius: Float,
        startDegree: Float,
        endDegree: Float,
        color: GLColor,
        skewness: Float = 1f,
        precisionFactor: Float = 1f
    ) {
        this.center = center
        this.radius = radius
        this.startDegree = startDegree
        this.endDegree = endDegree
        this.color = color
        this.skewness = skewness
        this.precisionFactor = precisionFactor
        valid = startDegree < endDegree && radius > 0
        vertexBuffer = if (valid) {
            calculateVertexCount()
            floatBufferOf(makeVertices())
        } else {
            floatBufferOf()
            //            plog("Invalid points: startDegree", startDegree, "endDegree", endDegree)
        }
    }

    fun clear() {
        apply(PointF(), 0f, 0f, 0f, GLColor.Black)
    }

    private fun calculateVertexCount() {
        var a = radius * precisionFactor * parent.getQualityFactor() + 4f
//        var a = radius * precisionFactor * 33000f / BaseRenderer.displayMinDim.toFloat() + 4f
        a *= (endDegree - startDegree) / 90f
        round(a).toInt().let {
            vertexCount = if (it < 3) 3 else it
            if (parent.highQuality())
                vertexCount *= 2
        }
    }

    private fun makeVertices(): FloatArray {
        val vertices = FloatArray(vertexCount * 3)
        val centerX = center.x
        val centerY = center.y
        vertices[0] = centerX
        vertices[1] = centerY
        vertices[2] = 0f

        val startRad = startDegree.toRadians()
        var x = radius * cos(startRad)
        var y = radius * sin(startRad)

        val theta = (endDegree - startDegree).toRadians() / (vertexCount - 2)
        val c = cos(theta)
        val s = sin(theta)

        var index = COORDS_PER_VERTEX
        val yGain = skewness
        var tempX: Float
        (2..vertexCount).forEach { _ ->
            vertices[index++] = centerX + x
            vertices[index++] = centerY + y * yGain
            vertices[index++] = 0f
            tempX = x
            x = c * tempX - s * y
            y = s * tempX + c * y
        }
        return vertices
    }

    override fun draw(mvpMatrix: FloatArray) {
        if (valid) {
            GLES20.glUseProgram(program)
            positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
                GLES20.glEnableVertexAttribArray(it)
                GLES20.glVertexAttribPointer(
                    it, 3, GLES20.GL_FLOAT, false, VertexStride, vertexBuffer
                )
                colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
                    GLES20.glUniform4fv(colorHandle, 1, color.toArray(), 0)
                }
                vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
                GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
                GLES20.glDisableVertexAttribArray(it)
            }
        }
    }
}
