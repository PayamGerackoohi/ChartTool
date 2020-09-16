package com.payam1991gr.chart.tool.shape

import android.graphics.PointF
import com.payam1991gr.chart.tool.renderer.IShapeParent
import com.payam1991gr.chart.tool.util.GLColor
import com.payam1991gr.chart.tool.util.div
import com.payam1991gr.chart.tool.util.lessThan
import com.payam1991gr.chart.tool.util.plus
import kotlin.math.sin
import kotlin.math.sqrt

class Bar(parent: IShapeParent) : BaseShape(parent) {
    private var start = PointF()
    private var end = PointF()
    private var radius = 0f
    private var color = GLColor.Black
    var fixStartPoint: PointF? = null
    var fixEndPoint: PointF? = null
    var basePoint: PointF? = null

    private val rectangle1 = Rectangle(parent)
    private val rectangle2 = Rectangle(parent)
    private val rectangle3 = Rectangle(parent)
    private val arc1 = Arc(parent)
    private val arc2 = Arc(parent)
    private val arc3 = Arc(parent)
    private val arc4 = Arc(parent)

    fun apply(start: PointF, end: PointF, radius: Float, color: GLColor) {
        this.start = start
        this.end = end
        this.radius = radius
        this.color = color
        if (start.lessThan(end)) {
            val r = if (radius > 0) radius else 0f
            val xs = start.x
            val xe = end.x
            val ys = start.y
            val ye = end.y
            val xd = xe - xs
            val yd = ye - ys
            val xsd = xs + r
            val xed = xe - r
            val dy = r
            val ysd = ys + dy
            val yed = ye - dy
            val ym = (ye + ys) / 2f
            val minXRad = xd / 2
            val pr = r / minXRad
            rectangle1.apply(PointF(xsd, ys), PointF(xed, ye), color)
            if (xed > xsd) {
                rectangle2.apply(PointF(xs, ysd), PointF(xsd, yed), color)
                rectangle3.apply(PointF(xed, ysd), PointF(xe, yed), color)
                if (yed > ysd) {
                    if (radius > 0) {
                        arc1.apply(PointF(xed, yed), radius, 0f, 90f, color)
                        arc2.apply(PointF(xsd, yed), radius, 90f, 180f, color)
                        arc3.apply(PointF(xsd, ysd), radius, 180f, 270f, color)
                        arc4.apply(PointF(xed, ysd), radius, 270f, 360f, color)
                    } else {
                        arc1.clear()
                        arc2.clear()
                        arc3.clear()
                        arc4.clear()
                    }
                } else {
                    if (yd > 0) {
                        if (radius > 0) {
                            val skewness = yd / radius / 2
                            arc1.apply(PointF(xed, ym), radius, -90f, 90f, color, skewness)
                            arc2.apply(PointF(xsd, ym), radius, 90f, 270f, color, skewness)
                        } else {
                            arc1.clear()
                            arc2.clear()
                        }
                    } else {
                        arc1.clear()
                        arc2.clear()
                    }
                    arc3.clear()
                    arc4.clear()
                }
            } else {
                val xm = (xe + xs) / 2f
                if (yed > ysd) {
                    rectangle2.apply(PointF(xs, ysd), PointF(xe, yed), color)
                    if (xd > 0 && radius > 0) {
                        val skewness = 2 * radius / xd
                        arc1.apply(PointF(xm, yed), minXRad, 0f, 180f, color, skewness, pr)
                        arc2.apply(PointF(xm, ysd), minXRad, 180f, 360f, color, skewness, pr)
                    } else {
                        arc1.clear()
                        arc2.clear()
                    }
                    arc3.clear()
                    arc4.clear()
                } else {
                    rectangle2.clear()
                    if (radius > 0)
                        arc1.apply(PointF(xm, ym), minXRad, 0f, 360f, color, yd / xd, pr)
                    else
                        arc1.clear()
                    arc2.clear()
                    arc3.clear()
                    arc4.clear()
                }
                rectangle3.clear()
            }
        } else {
            rectangle1.clear()
            rectangle2.clear()
            rectangle3.clear()
            arc1.clear()
            arc2.clear()
            arc3.clear()
            arc4.clear()
        }
    }

    override fun draw(mvpMatrix: FloatArray) {
        rectangle1.draw(mvpMatrix)
        rectangle2.draw(mvpMatrix)
        rectangle3.draw(mvpMatrix)
        arc1.draw(mvpMatrix)
        arc2.draw(mvpMatrix)
        arc3.draw(mvpMatrix)
        arc4.draw(mvpMatrix)
    }

    fun updateRadius(newRadius: Float) {
        apply(start, end, newRadius, color)
    }

    fun refresh() {
        apply(start, end, radius, color)
    }

    fun fixPoints() {
        fixStartPoint = start
        fixEndPoint = end
    }

    fun fixApply(ratio: Float, base: Float) {
        fixStartPoint?.let { start = PointF(it.x, base + ratio * (it.y - base)) }
        fixEndPoint?.let { end = PointF(it.x, base + ratio * (it.y - base)) }
        apply(start, end, radius, color)
    }

    fun fixedCenter(): PointF? {
        return (fixStartPoint + fixEndPoint) / 2f
    }
}
