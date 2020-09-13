package com.payam1991gr.chart.tool.shape

import android.opengl.GLES20
import androidx.annotation.RawRes
import com.payam1991gr.chart.tool.R
import com.payam1991gr.chart.tool.renderer.IShapeParent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

@Suppress("SameParameterValue", "unused")
abstract class BaseShape(private val parent: IShapeParent) {
    @RawRes
    protected var vertexShader: Int = R.raw.vertex_shader
    @RawRes
    protected var fragmentShader: Int = R.raw.fragment_shader

    protected var program: Int = 0

    protected fun setupShaders() {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun setupShaders(@RawRes vertexShaderRes: Int, @RawRes fragmentShaderRes: Int) {
        vertexShader = vertexShaderRes
        fragmentShader = fragmentShaderRes
        setupShaders()
    }

    private fun loadShader(type: Int, @RawRes shaderRes: Int): Int {
        return GLES20.glCreateShader(type).also { shader ->
            val shaderCode = parent.getShaderCode(shaderRes)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    protected fun shortBufferOf(): ShortBuffer = ByteBuffer.allocateDirect(0).asShortBuffer()
    protected fun shortBufferOf(array: ShortArray): ShortBuffer =
        ByteBuffer.allocateDirect(array.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(array)
                position(0)
            }
        }

    protected fun floatBufferOf(): FloatBuffer = ByteBuffer.allocateDirect(0).asFloatBuffer()
    protected fun floatBufferOf(array: FloatArray): FloatBuffer =
        ByteBuffer.allocateDirect(array.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(array)
                position(0)
            }
        }

    abstract fun draw(mvpMatrix: FloatArray)
}
