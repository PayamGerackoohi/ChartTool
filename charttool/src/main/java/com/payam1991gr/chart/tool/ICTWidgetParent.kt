package com.payam1991gr.chart.tool

import android.graphics.Typeface
import com.payam1991gr.chart.tool.data.CTData

interface ICTWidgetParent {
    fun getRtl(): Boolean
    fun getTypeface(): Typeface?
    fun getFontSize(): Int?
    fun dataAt(index: Int): CTData?
    fun categoryAt(index: Int): String?
    fun seriesCount(): Int
}
