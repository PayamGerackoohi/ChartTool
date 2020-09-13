package com.payam1991gr.chart.tool.util

import com.payam1991gr.chart.tool.util.Constants.Companion.DebugMode
import com.payam1991gr.chart.tool.util.Constants.Companion.LibName


fun plog() = baseLog("is called.")
fun plog(message: String) = baseLog(message)
fun plog(vararg args: Any) {
    val formattedMessage = StringBuilder()
    val valueList = ArrayList<Any>()
    var first = true
    for (o in args) {
        if (first) {
            formattedMessage.append(o)
        } else {
            formattedMessage.append("<%s> ")
            valueList.add(o)
        }
        first = !first
    }
    val messageFormat = formattedMessage.toString()
    if (messageFormat.isNotEmpty())
        baseLog(String.format(messageFormat, *valueList.toTypedArray()))
}

private fun baseLog(message: String) {
    if (DebugMode) {
        val st = Thread.currentThread().stackTrace[4]
        val methodName = st.methodName
        val cName = st.className
        val className = cName!!.substring(cName.indexOfLast { it == '.' } + 1)
        println("****** $LibName ****** $className::$methodName: $message")
    }
}
