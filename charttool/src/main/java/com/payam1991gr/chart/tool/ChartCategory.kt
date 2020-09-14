package com.payam1991gr.chart.tool

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.RequiresApi

class ChartCategory : ViewGroup {
    private val categories = ArrayList<String>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}

    fun setCategories(list: List<String>) {
        categories.clear()
        categories.addAll(list)
    }

    fun appear() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun disappear() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
