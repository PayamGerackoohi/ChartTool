package com.payam1991gr.chart.tool.util

data class GLColor(val r: Float, val g: Float, val b: Float, val a: Float) {
    companion object {
        val Black = GLColor(0f, 0f, 0f, 1f)
        val White = GLColor(1f, 1f, 1f, 1f)
        val Red = GLColor(1f, 0f, 0f, 1f)
        val Green = GLColor(0f, 1f, 0f, 1f)
        val Blue = GLColor(0f, 0f, 1f, 1f)
    }

    fun toArray(): FloatArray = floatArrayOf(r, g, b, a)
}
