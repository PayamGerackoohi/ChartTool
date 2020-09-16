package com.payam1991gr.chart.tool.app.ui.charttest

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.payam1991gr.chart.tool.app.R
import com.payam1991gr.chart.tool.app.ui.base.BasePage
import com.payam1991gr.chart.tool.data.CTData
import kotlinx.android.synthetic.main.page_chart_test.*

class ChartTestPage : BasePage() {
    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, ChartTestPage::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_chart_test)
        setupUI()
    }

    private fun setupUI() {
        chart1Test()
        chart2Test()
    }

    private fun chart1Test() {
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

    private fun chart2Test() {
        chart2
            .data(
                CTData()
                    .nameId(R.string.legend1)
                    // todo: .values(arrayListOf(2f, 4f, 3f))
                    .values(arrayListOf(2, 4, 3))
                    .labels(arrayListOf("2", "4", "3"))
                    .color(Color.RED)
            )
            .categories(arrayListOf("Jan", "Feb", "Mar"))
            .show()
    }
}
