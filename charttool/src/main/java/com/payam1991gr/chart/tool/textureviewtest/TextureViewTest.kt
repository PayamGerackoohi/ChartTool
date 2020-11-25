package com.payam1991gr.chart.tool.textureviewtest

import android.annotation.TargetApi
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.TextureView
import android.graphics.drawable.Drawable
import androidx.annotation.RawRes
import com.payam1991gr.chart.tool.util.getRawResString
import com.payam1991gr.chart.tool.util.plog

class TextureViewTest : TextureView, TextureView.SurfaceTextureListener, IRendererParent {
    private lateinit var renderer: TextureViewTestRenderer

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    override fun setBackgroundDrawable(background: Drawable?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && background != null) {
            setBackgroundDrawable(background)
        }
    }

    init {
        surfaceTextureListener = this
//        checkDimensions()
    }

//    private fun checkDimensions() {
//        viewTreeObserver.addOnGlobalLayoutListener {
//            plog("width", width, "height", height)
//        }
//    }

    // called every time when swapBuffers is called
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        renderer.onSurfaceUpdated()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        plog("width", width, "height", height)
        renderer.onSizeChanged(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
//        plog()
        renderer.isStopped = true
        return false // surface.release() manually, after the last render
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        plog("width", width, "height", height)
        renderer = TextureViewTestRenderer(this, surface, width, height)
        renderer.start()
    }

    override fun getShaderCode(@RawRes shaderRes: Int): String = context.getRawResString(shaderRes)
}
