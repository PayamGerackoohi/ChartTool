package com.payam1991gr.chart.tool.app.ui.animationtest

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.payam1991gr.chart.tool.app.R
import com.payam1991gr.chart.tool.app.ui.base.BasePage
import com.payam1991gr.chart.tool.app.util.DisplayUtils
import com.payam1991gr.chart.tool.data.CTData
import kotlinx.android.synthetic.main.page_animation_test.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class AnimationTestPage : BasePage() {
    companion object {
        private const val SPACE_MAX_RATIO = .4f
        fun instance(context: Context): Intent {
            return Intent(context, AnimationTestPage::class.java)
        }
    }

    enum class SpaceAxis {
        Horizontal, Vertical
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainScope = CoroutineScope(SupervisorJob() + Main)
    private fun io(block: suspend CoroutineScope.() -> Unit) = scope.launch { block() }
    private fun ui(block: suspend CoroutineScope.() -> Unit) = mainScope.launch {
        try {
            block()
        } catch (e: Exception) {
        }
    }

    data class SpaceView(val view: View, var direction: Int, val axis: SpaceAxis, val period: Long)

    private var pageWidth = 0
    private var pageHeight = 0
    private var maxHorizontalMargin = 0
    private var maxVerticalMargin = 0
    private var allowAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_animation_test)
        setupUI()
    }

    private fun setupUI() {
        initPageDimensions()
        waitForViewDimensionReady(SpaceView(leftSpace, 1, SpaceAxis.Horizontal, 1L))
        waitForViewDimensionReady(SpaceView(rightSpace, -1, SpaceAxis.Horizontal, 2L))
        waitForViewDimensionReady(SpaceView(topSpace, 1, SpaceAxis.Vertical, 2L))
        waitForViewDimensionReady(SpaceView(bottomSpace, -1, SpaceAxis.Vertical, 1L))
        chartTest()
//        testChoreographer()
    }

//    private var lastTime = -1L
//    private fun testChoreographer() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            Choreographer.getInstance().postFrameCallback { time ->
//                if (lastTime != -1L)
//                    plog("deltaTime", time - lastTime)
//                lastTime = time
//            }
//        }
//    }

    private fun chartTest() {
        chart1.setLegendView(chart1Legend)
        chart1.setCategoryView(chart1Category)
        chart1.setLabelView(chart1Label)
        chart1.setTooltipView(chart1Tooltip)
//        chart1
//            .data(
//                CTData()
//                    .nameId(R.string.legend1)
//                    .values(arrayListOf(2, 4, 3))
//                    .labels(arrayListOf("2", "4", "3"))
//                    .color(Color.RED)
//            )
//            .categories(arrayListOf("Jan", "Feb", "Mar"))
//            .font("fonts/B-NAZANIN.TTF", this)
//            .fontSize(14)
//            .radius(6)
//            .show()
//
        chart1
            .data(
                CTData()
                    .name("سبز")
//                    .name("Sample 1")
//                    .values(arrayListOf(1, 1, 1, 1, 1, 1))
//                    .labels(arrayListOf("1", "1", "1", "1", "1", "1"))
                    .values(
                        arrayListOf(
//                        2, 4, 3, -1, 2, 5,
                            20, 40, 30, -10, 20, 50
                        )
                    )
                    .labels(
                        arrayListOf(
//                        "2", "4", "3", "-1", "2", "5",
                            "20", "40", "30", "-10", "20", "50"
                        )
                    )
                    .colorId(R.color.green),
//                    .color(GLColor.Green),
                CTData()
                    .name("آبی")
//                    .name("s2")
//                    .values(arrayListOf(2, 2, 2, 2, 2, 2))
//                    .labels(arrayListOf("2", "2", "2", "2", "2", "2"))
                    .values(
                        arrayListOf(
//                        3, -2, 4, -3, 5, 2,
                            30, -20, 40, -30, 50, 20
                        )
                    )
                    .labels(
                        arrayListOf(
//                        "3", "-2", "4", "-3", "5", "2",
                            "30", "-20", "40", "-30", "50", "20"
                        )
                    )
                    .colorId(R.color.blue),
//                    .color(GLColor.Blue),
                CTData()
//                    .name("Legend Sample 3")
                    .name("قرمز")
//                    .values(arrayListOf(3, 3, 3, 3, 3, 3))
//                    .labels(arrayListOf("3", "3", "3", "3", "3", "3"))
                    .values(
                        arrayListOf(
//                        -5, 3, 2, 4, 1, 3,
                            -50, 30, 20, 40, 10, 30
                        )
                    )
                    .labels(
                        arrayListOf(
//                        "-5", "3", "2", "4", "1", "3",
                            "-50", "30", "20", "40", "10", "30"
                        )
                    )
                    .colorId(R.color.red)
//                    .color(GLColor.Red)
            )
            .categories(
                arrayListOf(
//                "فروردین ۱۳۹۹", "اردیبهشت ۱۳۹۹", "خرداد ۱۳۹۹", "تیر ۱۳۹۹", "مرداد ۱۳۹۹", "شهریور ۱۳۹۹",
                    "فروردین ۱۳۹۹", "اردیبهشت ۱۳۹۹", "خرداد ۱۳۹۹", "تیر ۱۳۹۹", "مرداد ۱۳۹۹", "شهریور ۱۳۹۹"
                )
            )
//            .categories(arrayListOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"))
            .rtl()
            .font("fonts/B-NAZANIN.TTF", this)
            .fontSize(16)
            .radius(6)
//            .highQuality()
            .show()
    }

    private fun initPageDimensions() {
        pageWidth = DisplayUtils.getWidth(this)
        pageHeight = DisplayUtils.getHeight(this)
        maxHorizontalMargin = (pageWidth * SPACE_MAX_RATIO).toInt()
        maxVerticalMargin = (pageHeight * SPACE_MAX_RATIO).toInt()
    }

    private fun waitForViewDimensionReady(spaceView: SpaceView) {
        spaceView.view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (spaceView.view.width > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        spaceView.view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        spaceView.view.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                    animateView(spaceView)
                }
            }
        })
    }

    private fun animateView(spaceView: SpaceView) {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                ui {
                    if (allowAnimation) {
                        spaceView.view.layoutParams = spaceView.view.layoutParams.apply {
                            if (spaceView.axis == SpaceAxis.Horizontal) {
                                var newWidth = width + spaceView.direction
                                if (newWidth > maxHorizontalMargin) {
                                    newWidth = maxHorizontalMargin
                                    spaceView.direction = -spaceView.direction
                                } else if (newWidth < 1) {
                                    newWidth = 1
                                    spaceView.direction = -spaceView.direction
                                }
                                width = newWidth
                            } else {
                                var newHeight = height + spaceView.direction
                                if (newHeight > maxVerticalMargin) {
                                    newHeight = maxVerticalMargin
                                    spaceView.direction = -spaceView.direction
                                } else if (newHeight < 1) {
                                    newHeight = 1
                                    spaceView.direction = -spaceView.direction
                                }
                                height = newHeight
                            }
                        }
                    }
                }
            }
        }, 0L, spaceView.period)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> allowAnimation = true
            MotionEvent.ACTION_UP -> allowAnimation = false
        }
        return super.onTouchEvent(event)
    }
}
