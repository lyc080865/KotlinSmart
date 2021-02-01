package com.kotlin.smart.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author  lyc
 * @date    2021/1/26
 * @description:
 */
public class AppFrontBackHelper {
    var mOnAppStatusListener: OnAppStatusListener? = null;

    constructor() {

    }

    fun register(application: Application, listener: OnAppStatusListener) {
        this.mOnAppStatusListener = listener
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun unRegister(application: Application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }


    val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        var activityStartCount = 0;

        override fun onActivityPaused(activity: Activity) {
            TODO("Not yet implemented")
        }

        override fun onActivityStarted(activity: Activity) {
            activityStartCount++
            if (activityStartCount == 1) {
                mOnAppStatusListener?.onFront()
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            TODO("Not yet implemented")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            TODO("Not yet implemented")
        }

        override fun onActivityStopped(activity: Activity) {
           activityStartCount--
            if (activityStartCount ==0){
                mOnAppStatusListener?.onBack()
            }
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onActivityResumed(activity: Activity) {
            TODO("Not yet implemented")
        }
    }


    interface OnAppStatusListener {
        fun onFront()
        fun onBack()
    }

}