package com.payam1991gr.chart.tool.data

import android.graphics.Typeface

interface ICTWidgetParent {
    fun getRtl(): Boolean
    fun getTypeface(): Typeface?
    fun getFontSize(): Int?
    fun dataAt(index: Int): CTData?
    fun categoryAt(index: Int): String?
    fun seriesCount(): Int
}
