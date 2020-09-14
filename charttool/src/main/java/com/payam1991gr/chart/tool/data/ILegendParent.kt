package com.payam1991gr.chart.tool.data

import android.graphics.Typeface

interface ILegendParent {
    fun getRtl(): Boolean
    fun getTypeface(): Typeface?
    fun getFontSize(): Int?
}
