package com.payam1991gr.chart.tool.renderer

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import com.payam1991gr.chart.tool.util.plog
import java.util.concurrent.locks.ReentrantLock
import javax.microedition.khronos.egl.*
import kotlin.concurrent.withLock

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseTextureViewRenderer(
    private val parent: RendererParent,
    private val surface: SurfaceTexture,
    protected var width: Int,
    protected var height: Int
) : Thread(), IShapeParent {
    companion object {
        private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        private const val EGL_OPENGL_ES2_BIT = 0x0004
//        private const val ANIMATION_DELTA_TIME = (1000f / 1f).toLong()
        private const val ANIMATION_DELTA_TIME = (1000f / 60f).toLong()
    }

    var isStopped = false
    private lateinit var egl: EGL10
    private lateinit var eglDisplay: EGLDisplay
    private lateinit var eglSurface: EGLSurface
    private lateinit var eglContext: EGLContext
    private var frameLock = ReentrantLock(true)
    private var frameWatcher = frameLock.newCondition()
    var highQuality = false
    protected var displayMinDim: Int = 0

    protected val projectionMatrix = FloatArray(16)
    protected val viewMatrix = FloatArray(16)
    protected val vPMatrix = FloatArray(16)
    protected var displayRatio = 1f
    protected var invDisplayRatio = 1f
    private var dimensionChanged = false

    private val config = intArrayOf(
        EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        EGL10.EGL_RED_SIZE, 8,
        EGL10.EGL_GREEN_SIZE, 8,
        EGL10.EGL_BLUE_SIZE, 8,
        EGL10.EGL_ALPHA_SIZE, 8,
        EGL10.EGL_DEPTH_SIZE, 0,
        EGL10.EGL_STENCIL_SIZE, 0,
        EGL10.EGL_NONE
    )

    override fun run() {
        super.run()
        configEGL()
        drawFirstFrame()

        while (!isStopped && egl.eglGetError() == EGL10.EGL_SUCCESS) {
            onFrame()
//            frameLock.withLock { frameWatcher.await() }
            sleep(ANIMATION_DELTA_TIME)
        }

        surface.release()
        egl.eglDestroyContext(eglDisplay, eglContext)
        egl.eglDestroySurface(eglDisplay, eglSurface)
    }

    private fun drawFirstFrame() {
        egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        onInit()
        applyDimensions()
        egl.eglSwapBuffers(eglDisplay, eglSurface)
    }

    private fun onFrame() {
        egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        if (dimensionChanged) {
            dimensionChanged = false
            applyDimensions()
        }
        onDraw()
        egl.eglSwapBuffers(eglDisplay, eglSurface)
    }

    private fun configEGL() {
        egl = EGLContext.getEGL() as EGL10
        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        egl.eglInitialize(eglDisplay, version)
//        plog("EGL version[0]", version[0], "version[1]", version[1])
        val eglConfig = chooseEglConfig(egl, eglDisplay)
        eglContext =
            egl.eglCreateContext(
                eglDisplay, eglConfig,
                EGL10.EGL_NO_CONTEXT,
                intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
            )
        eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surface, null)
    }

    private fun chooseEglConfig(egl: EGL10, eglDisplay: EGLDisplay): EGLConfig {
        val configsCount = intArrayOf(0)
        val configs = arrayOfNulls<EGLConfig>(1)
        egl.eglChooseConfig(eglDisplay, config, configs, 1, configsCount)
        return configs[0]!!
    }

    protected open fun applyDimensions() {
        plog("width", width, "height", height)
        GLES20.glViewport(0, 0, width, height)
        displayRatio = width.toFloat() / height.toFloat()
        invDisplayRatio = 1 / displayRatio
        displayMinDim = if (width < height) width else height
        (invDisplayRatio / 2f).let { Matrix.frustumM(projectionMatrix, 0, .5f, -.5f, -it, it, it, invDisplayRatio * 2f) }
//        plog("invDisplayRatio", invDisplayRatio)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -invDisplayRatio, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun onSizeChanged(width: Int, height: Int) {
        plog("width", width, "height", height)
        this.width = width
        this.height = height
//        applyDimensions()
        dimensionChanged = true
//        refresh()
    }

    fun refresh() {
        frameLock.withLock { frameWatcher.signal() }
    }

    override fun getShaderCode(shaderRes: Int): String = parent.getShaderCode(shaderRes)
    override fun getQualityFactor(): Float = 33000f / displayMinDim.toFloat()
    override fun highQuality(): Boolean = highQuality

    protected abstract fun onInit()
    protected abstract fun onDraw()
}
