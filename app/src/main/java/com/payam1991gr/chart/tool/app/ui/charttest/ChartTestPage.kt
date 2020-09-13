package com.payam1991gr.chart.tool.app.ui.charttest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.payam1991gr.chart.tool.app.R
import com.payam1991gr.chart.tool.app.ui.base.BasePage

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
    }
}
