package com.payam1991gr.chart.tool.data

import android.graphics.Typeface

interface ICTWidgetParent {
    fun getRtl(): Boolean
    fun getTypeface(): Typeface?
    fun getFontSize(): Int?
}
