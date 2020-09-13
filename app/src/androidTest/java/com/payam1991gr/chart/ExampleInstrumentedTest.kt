package com.payam1991gr.chart

import android.graphics.PointF
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.payam1991gr.chart.tool.app.util.plog

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
        assertEquals("com.payam1991gr.chart.tool", appContext.packageName)
    }

    @Test
    fun tempTest() {
        val center = PointF(0f, 0f)
        val cx = center.x
        val cy = center.y
        val d = 0.8f
        val p1 = PointF(cx + d, cy)
        val p2 = PointF(cx + 0.6f * d, cy - 0.3f * d)
        val p3 = PointF(cx + 0.3f * d, cy - 0.6f * d)
        val p4 = PointF(cx, cy - d)
        val arr = floatArrayOf(
            center.x, center.y, 0f,
            p1.x, p1.y, 0f,
            p2.x, p2.y, 0f,
            p3.x, p3.y, 0f,
            p4.x, p4.y, 0f
        )
        var str = "("
        for (i in arr.indices) {
            str += arr[i]
            if (i % 3 == 2) {
                str += ")"
                plog(str)
                str = "("
            } else {
                str += ", "
            }
        }
    }
}
