package com.payam1991gr.chart.tool

import android.graphics.Point
import androidx.annotation.RawRes
import com.payam1991gr.chart.tool.renderer.RendererParent
import java.util.ArrayList

interface IRendererParent : RendererParent {
    fun setLabelCoords(coords: List<Point?>)
    fun onFrameChanged()
    fun setToolTipData(tooltipData: List<Int?>)
}
