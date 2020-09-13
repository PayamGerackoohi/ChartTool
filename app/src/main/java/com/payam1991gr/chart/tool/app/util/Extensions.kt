package com.payam1991gr.chart.tool.app.util

import android.graphics.PointF
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI

fun now() = System.currentTimeMillis()

fun String.safeToInt(): Int {
    return try {
        this.toInt()
    } catch (e: Exception) {
        0
    }
}

fun String.toCurrency(): String {
    return this.safeToInt().toCurrency()
}

fun Int.toCurrency(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun PointF.lessThan(other: PointF): Boolean = x < other.x && y < other.y
fun PointF.lessThanOrEqual(other: PointF): Boolean = x <= other.x && y <= other.y

fun linSpace(start: Float, end: Float, num: Int): List<Float> {
    return if (num > 1) {
        val step = (end - start) / (num - 1)
        (0 until num).map { start + it * step }
    } else
        ArrayList()
}

const val d2rF = PI.toFloat() / 180f
fun Float.toRadians(): Float = this * d2rF
