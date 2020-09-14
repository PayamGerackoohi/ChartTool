package com.payam1991gr.chart.tool.app.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.payam1991gr.chart.tool.app.R
import com.payam1991gr.chart.tool.app.ui.bartest.BarTestPage
import com.payam1991gr.chart.tool.app.ui.charttest.ChartTestPage

import kotlinx.android.synthetic.main.page_home.*

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_home)
        openChartTestPage()
//        openBarTestPage()
        setupUI()
    }

    private fun setupUI() {
        setupChartTestButton()
        setupBarTestButton()
    }

    private fun setupChartTestButton() {
        chartTestButton.setOnClickListener { openChartTestPage() }
    }

    private fun setupBarTestButton() {
        barTestButton.setOnClickListener { openBarTestPage() }
    }

    private fun openBarTestPage() {
        startActivity(BarTestPage.instance(this))
    }

    private fun openChartTestPage() {
        startActivity(ChartTestPage.instance(this))
    }
}
