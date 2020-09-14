package com.payam1991gr.chart.tool

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.payam1991gr.chart.tool.data.CTData
import com.payam1991gr.chart.tool.renderer.ChartRenderer
import com.payam1991gr.chart.tool.util.DisplayUtils
import com.payam1991gr.chart.tool.util.getRawResString
import com.payam1991gr.chart.tool.util.plog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ChartView @JvmOverloads constructor(context: Context? = null, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs), IRendererParent {
    companion object {
        //        private const val ROTATION_TOUCH_SCALE_FACTOR: Float = 0.0625f
//        private const val ANIMATION_DURATION = 500L
        private const val ANIMATION_DURATION = 250L
    }
    //    private var previousX: Float = 0f
//    private var previousY: Float = 0f
//    private var cornerRadius = false

    private var dataList = ArrayList<CTData>()
    private var categories = ArrayList<String>()

    private val renderer: ChartRenderer
    private var animating = false

    init {
        setEGLContextClientVersion(2)
        renderer = ChartRenderer(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        setOnClickListener { animateChart() }
    }

    override fun getShaderCode(@RawRes shaderRes: Int): String = context.getRawResString(shaderRes)

    fun data(vararg data: CTData): ChartView {
        this.dataList.apply {
            clear()
            addAll(data)
        }
        return this
    }

    fun categories(categories: List<String>): ChartView {
        this.categories.apply {
            clear()
            addAll(categories)
        }
        return this
    }

    fun show() {
        if (height > 0)
            startPlot()
        else
            getHeightLazy()
    }

    private fun getHeightLazy() {
        Handler().postDelayed({
            if (height > 0)
                startPlot()
            else
                getHeightLazy()
        }, 100)
    }

    private fun startPlot() {
        gatherResources()
        drawLegends()
        drawLabels()
        drawBaseLine()
        drawValueBar()
        drawBars()
    }

    private fun gatherResources() {
        dataList.forEach { data ->
            data.nameId?.let { data.name(context.getString(it)) }
            data.colorId?.let { data.color(ContextCompat.getColor(context, it)) }
        }
    }

    private fun drawBars() {
        renderer.consumeData(dataList)
        requestRender()
        GlobalScope.launch {
            Thread.sleep(500)
            animateChart()
        }
    }

    private fun drawValueBar() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawBaseLine() {
        var count = 0
        dataList.forEach { data ->
            data.values.apply {
                if (count < size)
                    count = size
            }
        }
        var min = 0
        var max = 0
        (0 until count).forEach { index ->
            var localMin = 0
            var localMax = 0
            dataList.forEach { data ->
                data.values.getOrNull(index)?.let { value ->
                    if (value < 0) {
                        localMin += value
                        if (min > localMin)
                            min = localMin
                    } else {
                        localMax += value
                        if (max < localMax)
                            max = localMax
                    }

                }
            }
        }
        plog("count", count, "min", min, "max", max)
        if (count == 0) {
            plog("No Data")
            // todo: show user
        } else {
            renderer.drawBaseLine(DisplayUtils.convertDpToPixel(2 * 16) / height.toFloat(), min, max)
            renderer.count = count
            requestRender()
        }
    }

    private fun drawLabels() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawLegends() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun animateChart() {
        if (animating)
            return
        animating = true
        GlobalScope.launch {
            val timeStep = 1L
//            val timeStep = 30L
            var time = 0L
            while (time < ANIMATION_DURATION) {
                Thread.sleep(timeStep)
                val ratio = time / ANIMATION_DURATION.toFloat()
//                plog("ratio", ratio, "time", time)
                try {
                    renderer.animate(ratio)
                    requestRender()
                } catch (e: Exception) {
                    plog("Error", e.message ?: "?")
                }
                time += timeStep
            }
            renderer.animate(1f)
            requestRender()
            animating = false
        }
    }
}

//    override fun performClick(): Boolean {
//        super.performClick()
//        notifyUser("Chart Changed!")
//        return true
//    }
//
//    @Suppress("SameParameterValue")
//    private fun notifyUser(message: String) {
////        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onTouchEvent(e: MotionEvent): Boolean {
////        todo: implement performClick
//        val x: Float = e.x
//        val y: Float = e.y
//
//        when (e.action) {
//            MotionEvent.ACTION_MOVE -> {
//                var dx: Float = x - previousX
//                var dy: Float = y - previousY
//                if (cornerRadius) {
//                    renderer.setScale(-dy / height)
//                } else {
//                    if (rotate) {
//                        if (y > height / 2)
//                            dx *= -1
//                        if (x < width / 2)
//                            dy *= -1
//                        renderer.angle += (dx + dy) * ROTATION_TOUCH_SCALE_FACTOR
//                    } else {
//                        renderer.setHVScale(dx / width, -dy / height)
//                    }
//                }
//                requestRender()
//            }
//            MotionEvent.ACTION_UP -> performClick()
//        }
//        previousX = x
//        previousY = y
//        return true
//    }
