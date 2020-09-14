package com.payam1991gr.chart.tool.shape

import android.graphics.PointF
import android.opengl.GLES20
import com.payam1991gr.chart.tool.renderer.IShapeParent
import com.payam1991gr.chart.tool.util.GLColor
import com.payam1991gr.chart.tool.util.notEqualTo
import com.payam1991gr.chart.tool.util.plog
import java.nio.FloatBuffer

class Line(parent: IShapeParent) : BaseShape(parent) {
    companion object {
        private const val COORDS_PER_VERTEX = 3
        //        private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices
        private const val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per float
    }

    private var start = PointF()
    private var end = PointF()
    private var color = GLColor.Black

    private var coords: FloatArray? = null
    private var vertexBuffer: FloatBuffer? = null

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var vPMatrixHandle: Int = 0

    private var valid = false

    init {
        setupShaders()
    }

    fun apply(start: PointF, end: PointF, color: GLColor) {
        this.start = start
        this.end = end
        this.color = color
        valid = start.notEqualTo(end)
        if (valid) {
            coords = calculateCoords(start, end)
            vertexBuffer = floatBufferOf(coords!!)
        } else {
            coords = floatArrayOf()
            vertexBuffer = floatBufferOf()
            plog("Invalid points: start", start, "end", end)
        }
    }

    fun clear() {
        apply(PointF(), PointF(), GLColor.Black)
    }

    private fun calculateCoords(start: PointF, end: PointF): FloatArray {
        return floatArrayOf(
            start.x, start.y, 0.0f,
            end.x, end.y, 0.0f
        )
    }

    override fun draw(mvpMatrix: FloatArray) {
        if (valid) {
            GLES20.glUseProgram(program)
            positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
                GLES20.glEnableVertexAttribArray(it)
                GLES20.glVertexAttribPointer(it, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
                colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
                    GLES20.glUniform4fv(colorHandle, 1, color.toArray(), 0)
                }
                vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
                GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
                GLES20.glDrawArrays(GLES20.GL_LINES, 0, coords!!.size)
                GLES20.glDisableVertexAttribArray(it)
            }
        }
    }
}
