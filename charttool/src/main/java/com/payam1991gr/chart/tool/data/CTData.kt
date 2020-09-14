package com.payam1991gr.chart.tool.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.payam1991gr.chart.tool.util.GLColor

class CTData {
    var name: String = ""
        private set
    var nameId: Int? = null
        private set
    var values = ArrayList<Int>()
        private set
    var labels = ArrayList<String>()
        private set
    var color = GLColor.Blue
        private set
    var colorId: Int? = null
        private set

    fun name(name: String): CTData {
        this.name = name
        return this
    }

    fun nameId(@StringRes nameId: Int): CTData {
        this.nameId = nameId
        return this
    }

    fun values(values: List<Int>): CTData {
        this.values.addAll(values)
        return this
    }

    fun labels(labels: List<String>): CTData {
        this.labels.addAll(labels)
        return this
    }

    fun color(color: GLColor): CTData {
        this.color = color
        return this
    }

    fun color(color: Int): CTData {
        this.color = GLColor.from(color)
        return this
    }

    fun colorId(@ColorRes colorId: Int): CTData {
        this.colorId = colorId
        return this
    }
}
