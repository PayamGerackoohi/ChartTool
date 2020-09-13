package com.payam1991gr.chart.tool

import androidx.annotation.RawRes

interface IRendererParent {
    fun getShaderCode(@RawRes shaderRes: Int): String
}
