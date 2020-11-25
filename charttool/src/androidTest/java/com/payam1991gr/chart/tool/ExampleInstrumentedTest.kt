package com.payam1991gr.chart.tool

import android.graphics.PointF
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.payam1991gr.chart.tool.util.div
import com.payam1991gr.chart.tool.util.plus
import com.payam1991gr.chart.tool.util.times

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.payam1991gr.chart.tool.test", appContext.packageName)
    }

    @Suppress("DIVISION_BY_ZERO")
    @Test
    fun floatPointExtension() {
        var a = PointF()
        var b = PointF()

        assertEquals(a + b, PointF(0f, 0f))

        a = PointF(3f, 4f)
        b = PointF(-2f, 7f)
        assertEquals(a + b, PointF(1f, 11f))
        assertEquals(a.plus(b), PointF(1f, 11f))

        a = PointF(3f, 4f)
        b = PointF(-2f, 7f)
        assertEquals(a / b, PointF(-1.5f, 4f / 7f))
        assertEquals(a.div(b), PointF(-1.5f, 4f / 7f))

        a = PointF(3f, 4f)
        b = PointF(-2f, 0f)
        assertEquals(a / b, PointF(-1.5f, 4f / 0f))

        var c: Float = -2f
        a = PointF(3f, 4f)
        assertEquals(a / c, PointF(-1.5f, -2f))
        assertEquals(a.div(c), PointF(-1.5f, -2f))

        a = PointF(3f, 4f)
        c = 0f
        assertEquals(a / c, PointF(3f / 0f, 4f / 0f))
        assertEquals(a.div(c), PointF(3f / 0f, 4f / 0f))

        a = PointF(3f, 4f)
        b = PointF(-2f, 7f)
        assertEquals(a * b, PointF(-6f, 28f))
        assertEquals(a.times(b), PointF(-6f, 28f))

        a = PointF(3f, 4f)
        c = -2f
        assertEquals(a * c, PointF(-6f, -8f))
        assertEquals(a.times(c), PointF(-6f, -8f))
    }
}
