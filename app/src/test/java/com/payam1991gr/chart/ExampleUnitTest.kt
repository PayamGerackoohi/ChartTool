package com.payam1991gr.chart

import com.payam1991gr.chart.tool.app.util.linSpace
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.round

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun linSpaceTest() {
        var result = linSpace(0f, 5f, 5)
        assertEquals(result.size, 5)
        assertEquals(result[2], 2.5f)

        result = linSpace(0f, 1f, 5)
        assertEquals(result.size, 5)
        assertEquals(result[2], 0.5f)

        result = linSpace(0f, -1f, 5)
        assertEquals(result.size, 5)
        assertEquals(result[2], -0.5f)

        result = linSpace(0f, 10f, 2)
        assertEquals(result.size, 2)
        assertEquals(result[0], 0f)
        assertEquals(result[1], 10f)

        result = linSpace(0f, 10f, 1)
        assertEquals(result.size, 0)

        result = linSpace(0f, 10f, 0)
        assertEquals(result.size, 0)

        result = linSpace(0f, 10f, -1)
        assertEquals(result.size, 0)

        result = linSpace(0f, 0f, 5)
        assertEquals(result.size, 5)
        assertEquals(result[2], 0f)
    }

    @Test
    fun floatRound() {
        assertEquals(0f, round(0f), 0.01f)
        assertEquals(0f, round(0.1f), 0.01f)
        assertEquals(0f, round(-0.1f), 0.01f)
        assertEquals(0f, round(0.4f), 0.01f)
        assertEquals(0f, round(-0.4f), 0.01f)
        assertEquals(0f, round(0.5f), 0.01f)
        assertEquals(0f, round(-0.5f), 0.01f)
        assertEquals(1f, round(0.6f), 0.01f)
        assertEquals(-1f, round(-0.6f), 0.01f)
        assertEquals(1f, round(0.9f), 0.01f)
        assertEquals(-1f, round(-0.9f), 0.01f)
        assertEquals(1f, round(1f), 0.01f)
        assertEquals(-1f, round(-1f), 0.01f)
        assertEquals(1f, round(1.1f), 0.01f)
        assertEquals(-1f, round(-1.1f), 0.01f)
    }
}
