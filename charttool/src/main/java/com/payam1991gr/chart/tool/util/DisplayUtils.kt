package com.payam1991gr.chart.tool.util

import android.util.DisplayMetrics
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

class DisplayUtils {
    companion object {
        fun convertDpToPixel(dp: Int): Int {
            val displayMetrics = Resources.getSystem().displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).toInt()
        }

        fun getWidth(context: Context): Int {
            return displayMetrics(context).widthPixels
        }

        fun getHeight(context: Context): Int {
            return displayMetrics(context).heightPixels
        }

        private fun displayMetrics(context: Context): DisplayMetrics {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics
        }

        fun setWindowHeight(activity: Activity, dialog: Dialog, percent: Int) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val screenHeight = metrics.heightPixels
            val params = dialog.window?.attributes
            params?.height = screenHeight * percent / 100
            dialog.window?.attributes = params
        }

        fun dp2px(resources: Resources, dp: Float): Float {
            val scale = resources.displayMetrics.density
            return dp * scale + 0.5f
        }

        fun sp2px(resources: Resources, sp: Float): Float {
            val scale = resources.displayMetrics.scaledDensity
            return sp * scale
        }
    }
}
