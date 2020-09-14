package com.payam1991gr.chart.tool.app.ui.charttest

import android.content.Context
import android.content.Intent
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
        chart1
            .data(
                CTData()
                    .name("Legend 1")
                    .values(arrayListOf(2, 4, 3, -1, 2, 5))
                    .labels(arrayListOf("2:0", "4:0", "3:0", "-1", "2", "5"))
                    .colorId(R.color.green),
//                    .color(GLColor.Green),
                CTData()
                    .name("Legend 2")
                    .values(arrayListOf(3, -2, 4, -3, 5, 2))
                    .labels(arrayListOf("3:0", "-2:0", "4:0", "-3", "5", "2"))
                    .colorId(R.color.blue),
//                    .color(GLColor.Blue),
                CTData()
                    .name("Legend 3")
                    .values(arrayListOf(-5, 3, 2, 4, 1, 3))
                    .labels(arrayListOf("-5:0", "3:0", "2:0", "4", "1", "3"))
                    .colorId(R.color.red)
//                    .color(GLColor.Red)
            )
            .categories(arrayListOf("Jan", "Feb", "Mar"))
            .show()
    }

    private fun chart2Test() {
        chart2
            .data(
                CTData()
                    .nameId(R.string.legend1)
                    // todo: .values(arrayListOf(2f, 4f, 3f))
                    .values(arrayListOf(2, 4, 3))
                    .labels(arrayListOf("2:0", "4:0", "3:0"))
//                    .color(Color.RED)
            )
            .categories(arrayListOf("Jan", "Feb", "Mar"))
            .show()
    }
}
