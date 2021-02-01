package com.kotlin.smart.utils

import android.app.Activity
import android.util.DisplayMetrics

/**
 * @author  lyc
 * @date    2021/1/27
 * @description:
 */
class ScreenUtil {
    companion object {
        var metric: DisplayMetrics = DisplayMetrics()

        fun getWidth(activity: Activity): Int {
            activity.windowManager.defaultDisplay.getMetrics(metric)
            return metric.widthPixels
        }

        fun getHeight(activity: Activity): Int {
            activity.windowManager.defaultDisplay.getMetrics(metric)
            return metric.heightPixels
        }

    }
}