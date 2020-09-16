package com.payam1991gr.chart.tool.shape

import android.opengl.GLES20
import com.payam1991gr.chart.tool.renderer.IShapeParent
import java.nio.FloatBuffer

// todo: implement it
class Triangle(parent: IShapeParent) : BaseShape(parent) {
    companion object {
        private const val COORDS_PER_VERTEX = 3
        private var triangleCoords = floatArrayOf(     // in counterclockwise order:
            0.0f, 0.5773502691896257f, 0.0f,      // top
            -0.5f, -0.28867513459481287f, 0.0f,    // bottom left
            0.5f, -0.28867513459481287f, 0.0f      // bottom right
//            0.0f, 0.622008459f, 0.0f,      // top
//            -0.5f, -0.311004243f, 0.0f,    // bottom left
//            0.5f, -0.311004243f, 0.0f      // bottom right
        )
    }

    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private var vPMatrixHandle: Int = 0

    init {
        setupShaders()
    }

    private var vertexBuffer: FloatBuffer = floatBufferOf(triangleCoords)

    override fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }
            vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}
