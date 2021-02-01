package com.kotlin.smart.base

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kotlin.smart.utils.ScreenUtil
import java.lang.ref.WeakReference

/**
 * @author  lyc
 * @date    2021/1/27
 * @description:
 */
public abstract class BaseDialog : Dialog {
    var mContext: BaseActivity? = null
    var gravity: Int = Gravity.CENTER
    var closeTime = 60000
    var screen = 0.9
    var isHeightFull = false
    var mHander: Handler? = null

    class MyHandler(baseDialog: BaseDialog) : Handler() {
        var softReference: WeakReference<BaseDialog>? = null

        init {
            softReference = WeakReference(baseDialog)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    constructor(context: BaseActivity) : super(context) {
        mContext = context
        mHander = MyHandler(this)
    }

    constructor(context: BaseActivity, theme: Int) : super(context, theme) {
        mContext = context;
        mHander = MyHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(getContentView())
        setCanceledOnTouchOutside(false)

        initView()
        window?.attributes?.gravity = gravity

        try {
            val dividerID =
                mContext?.resources?.getIdentifier("android:id/titleDivider", null, null)
            val divier = dividerID?.let { findViewById<View>(it) }
            divier?.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDialogSize(){
        val p = window?.attributes
        //设置dialog的宽度为当前手机屏幕的宽度
        p!!.width = (ScreenUtil.getWidth(mContext!!) * screen).toInt()
        if (isHeightFull){
            p.height = WindowManager.LayoutParams.MATCH_PARENT
        }else{
            p.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        this.window?.attributes =p
    }

    protected abstract fun getContentView(): Int

    abstract fun initView()
}