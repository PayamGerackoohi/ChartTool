package com.payam1991gr.chart.tool.util

import android.content.Context
import android.graphics.PointF
import android.view.View
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.math.PI

const val d2rF = PI.toFloat() / 180f

fun now() = System.currentTimeMillis()

fun String.safeToInt(): Int {
    return try {
        this.toInt()
    } catch (e: Exception) {
        0
    }
}

fun PointF.lessThan(other: PointF): Boolean = x < other.x && y < other.y
fun PointF.lessThanOrEqual(other: PointF): Boolean = x <= other.x && y <= other.y
fun PointF.notEqualTo(other: PointF): Boolean = x != other.x || y != other.y
fun PointF.equalsTo(other: PointF): Boolean = x == other.x && y == other.y

fun linSpace(start: Float, end: Float, num: Int): List<Float> {
    return if (num > 1) {
        val step = (end - start) / (num - 1)
        (0 until num).map { start + it * step }
    } else
        ArrayList()
}

fun Float.toRadians(): Float = this * d2rF

fun Context.getRawResString(@RawRes rawRes: Int): String {
    val inputStream = resources.openRawResource(rawRes)
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val total = StringBuilder()
    var line: String?
    while (run { line = bufferedReader.readLine(); line != null }) {
        total.append(line).append('\n')
    }
    bufferedReader.close()
    inputStream.close()
    return total.toString()
}

fun View.show(show: Boolean) {
    if (show)
        show()
    else
        gone()
}

fun View.gone(gone: Boolean) {
    if (gone)
        gone()
    else
        show()
}

fun View.hide(hide: Boolean) {
    if (hide)
        hide()
    else
        show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}
