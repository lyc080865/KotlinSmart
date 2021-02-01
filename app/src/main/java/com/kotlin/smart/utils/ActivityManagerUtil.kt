package com.kotlin.smart.utils

import android.content.Intent
import com.kotlin.smart.base.BaseActivity
import java.util.*

/**
 * @author  lyc
 * @date    2021/1/19
 * @description:
 */
class ActivityManagerUtil {
    companion object {
        var activityStack: Stack<BaseActivity>? = null
        var activityManagerUtil: ActivityManagerUtil = ActivityManagerUtil()

        public fun getInstance(): ActivityManagerUtil {
            return activityManagerUtil
        }

        public fun showActivityStackInfo() {
            if (activityStack == null) {
                LogUtils.e("activityStack 为 null")
                return
            }
            LogUtils.e("activityStack size : " + activityStack!!.size)

            for (i in activityStack!!.indices) {
                LogUtils.e("activityStack $i : ${activityStack!![i]::class.simpleName.toString()}")
            }
            LogUtils.e("activityStack ////////////////////////////// ")
        }
    }

    /**
     * 将activity移出栈
     *
     * @param activity
     */
    fun popActivity(activity: BaseActivity?) {
        if (activity != null) {
            activityStack?.remove(activity)
        }
    }

    fun getActivity(cls: Class<out BaseActivity?>?): BaseActivity? {
        for (i in activityStack!!.indices.reversed()) {
            if (activityStack!![i].equals(cls)) {
                return activityStack!![i]
            }
        }
        return null
    }


    /**
     * 结束指定activity
     *
     * @param cls
     */
    fun finishActivity(cls: Class<out BaseActivity?>?) {
        var activity = getActivity(cls)
        if (activity != null) {
            activity.finish()
            activityStack?.remove(activity)
            activity = null
        }
    }


    /**
     * 结束指定activity
     *
     * @param activity
     */
    fun finishActivity(activity: BaseActivity?) {
        var activity = activity
        if (activity != null) {
            activity.finish()
            activityStack?.remove(activity)
            activity = null
        }
    }

    /**
     * 获得当前的activity(即最上层)
     *
     * @return
     */
    fun currentActivity(): BaseActivity? {
        var activity: BaseActivity? = null
        if (!activityStack!!.empty()) {
            activity = activityStack!!.lastElement()
        }
        return activity
    }

    /**
     * 将activity推入栈内
     *
     * @param activity
     */
    fun addActivity(activity: BaseActivity?) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 结束除cls之外的所有activity,执行结果都会清空Stack
     *
     * @param cls
     */
    fun finishAllActivityExceptOne(cls: Class<out BaseActivity?>?) {
        while (!activityStack!!.empty()) {
            val activity = currentActivity()
            if (activity!!.equals(cls)) {
                return
            } else {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        while (!activityStack!!.empty()) {
            val currentActivity = currentActivity()
            finishActivity(currentActivity)
        }
    }

    fun getTopActivity(): BaseActivity? {
        if (activityStack == null) {
            return null
        }

        return activityStack!!.get(activityStack!!.size - 1)
    }

    public fun openActivity(activity: BaseActivity?, cls: Class<out BaseActivity?>?) {
        activity!!.startActivity(Intent(activity, cls))
    }

    fun openActivity(cls: Class<out BaseActivity?>?) {
        currentActivity()!!.startActivity(Intent(currentActivity(), cls))
    }

}