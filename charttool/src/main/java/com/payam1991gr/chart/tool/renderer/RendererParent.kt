package com.payam1991gr.chart.tool.renderer

import androidx.annotation.RawRes

interface RendererParent {
    fun getShaderCode(@RawRes shaderRes: Int): String
}
