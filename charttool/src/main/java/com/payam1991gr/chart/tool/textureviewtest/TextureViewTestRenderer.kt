package com.payam1991gr.chart.tool.textureviewtest

import android.graphics.PointF
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.payam1991gr.chart.tool.renderer.BaseTextureViewRenderer
import com.payam1991gr.chart.tool.shape.Rectangle
import com.payam1991gr.chart.tool.util.GLColor
import com.payam1991gr.chart.tool.util.plog

class TextureViewTestRenderer(parent: IRendererParent, surface: SurfaceTexture, width: Int, height: Int) :
    BaseTextureViewRenderer(parent, surface, width, height) {
    private var rectangle: Rectangle? = null
    //    private var triangle: Triangle? = null
    //    private var colorVelocity = 0.01f
//    private var color = 0f

    override fun onInit() {
        plog()
//        colorVelocity = 0.01f
//        color = 0f
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        rectangle = Rectangle(this)
//        triangle = Triangle(this)
    }

    override fun onDraw() {
        plog()
//        if (color > 1 || color < 0) colorVelocity *= -1
//        color += colorVelocity
//
//        GLES20.glClearColor(color, color / 2, color, 1.0f)
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val d = .95f
        rectangle?.apply(PointF(-d, -d * invDisplayRatio), PointF(d, d * invDisplayRatio), GLColor.Red)
        rectangle?.draw(vPMatrix)
//        triangle?.draw(vPMatrix)
    }

    fun onSurfaceUpdated() {
        plog()
    }
}
