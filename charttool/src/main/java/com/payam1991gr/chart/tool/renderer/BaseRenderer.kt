package com.payam1991gr.chart.tool.renderer

abstract class BaseRenderer : IShapeParent {
    companion object {
        var HighQuality: Boolean = false
    }

    protected var displayMinDim: Int = 0
    override fun getQualityFactor(): Float = 33000f / displayMinDim.toFloat()
}
