package com.kotlin.smart.base

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import butterknife.Unbinder
import com.kotlin.smart.MyApplication
import com.kotlin.smart.constant.Constants
import com.kotlin.smart.view.BadNetErrorDialog
import com.kotlin.smart.view.LowPowerDialog
import com.trello.rxlifecycle3.components.support.RxFragmentActivity
import java.lang.ref.WeakReference

/**
 * @author  lyc
 * @date    2021/1/19
 * @description:
 */
open class BaseActivity : RxFragmentActivity() {
    var TAG = this.javaClass.simpleName
    val mHandler = MyHandler(this)
    protected var unbinder: Unbinder? = null
    var newConfig: Configuration? = null
    val lifeStatus: Int = 0
    var mMediaPlayer: MediaPlayer? = null
    var badNetErrorDialog: BadNetErrorDialog? = null

    class MyHandler(mainActivity: BaseActivity) : Handler() {
        var softReference: WeakReference<BaseActivity>? = null

        init {
            softReference = WeakReference<BaseActivity>(mainActivity)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val mainActivity = softReference?.get()
            if (mainActivity != null) {
                if (msg.what == Constants.HANDLER_DISMISS_DIALOG) {
                    mainActivity.dismissDialog()
                }
                mainActivity.handlerCallBack(msg)
            }
        }
    }

    public fun handlerCallBack(msg: Message) {

    }

    private fun dismissDialog() {
        TODO("Not yet implemented")
    }

    fun showPenLowPowerWindow() {
        if (!MyApplication.isOnFront) {
            return
        }
        showPenDialog(LowPowerDialog.PEN_LOW_POWER)
    }

    fun showPenDialog(type: Int) {

    }

    fun showNetErrorWindow() {

    }

    fun disPenLowPowerWindow() {

    }

    fun disNetErrorWindow() {

    }

    fun disBluetoothWindow() {

    }


}