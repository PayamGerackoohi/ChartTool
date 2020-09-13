package com.payam1991gr.chart.tool.app.ui.bartest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.payam1991gr.chart.tool.app.R
import com.payam1991gr.chart.tool.app.ui.base.BasePage
import com.payam1991gr.chart.tool.util.gone
import kotlinx.android.synthetic.main.page_bar_test.*

class BarTestPage : BasePage() {
    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, BarTestPage::class.java)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_bar_test)
        setupUI()
    }

    private fun setupUI() {
        cornerRadiusSwitch.setOnCheckedChangeListener { _, isChecked ->
            chart.setCornerRadius(isChecked)
            rotateSwitch.gone(isChecked)
            rotateLabel.gone(isChecked)
            space2.gone(isChecked)
        }
        rotateSwitch.setOnCheckedChangeListener { _, isChecked ->
            chart.setRotate(isChecked)
        }
        qualitySwitch.setOnCheckedChangeListener { _, isChecked ->
            chart.setHighQuality(isChecked)
        }
    }
}
