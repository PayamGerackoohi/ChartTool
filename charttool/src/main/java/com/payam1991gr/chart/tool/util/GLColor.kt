package com.payam1991gr.chart.tool.util

import android.graphics.Color

data class GLColor(val r: Float, val g: Float, val b: Float, val a: Float) {
    companion object {
        val Black = GLColor(0f, 0f, 0f, 1f)
        val White = GLColor(1f, 1f, 1f, 1f)
        val Red = GLColor(1f, 0f, 0f, 1f)
        val Green = GLColor(0f, 1f, 0f, 1f)
        val Blue = GLColor(0f, 0f, 1f, 1f)
        val Gray = GLColor(.27f, .27f, .27f, 1f)
        fun from(color: Int): GLColor =
            GLColor(Color.red(color) / 255f, Color.green(color) / 255f, Color.blue(color) / 255f, Color.alpha(color) / 255f)
    }

    fun toArray(): FloatArray = floatArrayOf(r, g, b, a)
}
