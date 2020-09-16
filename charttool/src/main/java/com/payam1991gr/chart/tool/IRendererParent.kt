package com.payam1991gr.chart.tool

import android.graphics.Point
import androidx.annotation.RawRes
import java.util.ArrayList

interface IRendererParent {
    fun getShaderCode(@RawRes shaderRes: Int): String
    fun setLabelCoords(coords: List<Point?>)
    fun onFrameChanged()
    fun setToolTipData(tooltipData: List<Int?>)
}
