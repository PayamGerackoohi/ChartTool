package com.payam1991gr.chart.tool

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RawRes
import com.payam1991gr.chart.tool.renderer.ChartRenderer
import com.payam1991gr.chart.tool.util.getRawResString

class ChartView @JvmOverloads constructor(context: Context? = null, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs), IRendererParent {
    companion object {
        //        private const val TOUCH_SCALE_FACTOR: Float = 0.5625f
        private const val ROTATION_TOUCH_SCALE_FACTOR: Float = 0.0625f
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val renderer: ChartRenderer
    private var cornerRadius = false
    private var rotate = false

    init {
        setEGLContextClientVersion(2)
        renderer = ChartRenderer(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

//    override fun performClick(): Boolean {
//        super.performClick()
//        notifyUser("Chart Changed!")
//        return true
//    }

    @Suppress("SameParameterValue")
    private fun notifyUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // todo: implement performClick
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx: Float = x - previousX
                var dy: Float = y - previousY
                if (cornerRadius) {
                    renderer.setScale(-dy / height)
                } else {
                    if (rotate) {
                        if (y > height / 2)
                            dx *= -1
                        if (x < width / 2)
                            dy *= -1
                        renderer.angle += (dx + dy) * ROTATION_TOUCH_SCALE_FACTOR
                    } else {
                        renderer.setHVScale(dx / width, -dy / height)
                    }
                }
                requestRender()
            }
            MotionEvent.ACTION_UP -> performClick()
        }
        previousX = x
        previousY = y
        return true
    }

    fun setCornerRadius(cornerRadius: Boolean) {
        this.cornerRadius = cornerRadius
        requestRender()
    }

    fun setRotate(rotate: Boolean) {
        this.rotate = rotate
        requestRender()
    }

    fun setHighQuality(highQuality: Boolean) {
        renderer.setHighQuality(highQuality)
        requestRender()
    }

    override fun getShaderCode(@RawRes shaderRes: Int): String = context.getRawResString(shaderRes)
}
