package com.kotlin.smart.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.kotlin.smart.R
import com.kotlin.smart.activity.BluetoothConnectionActivity

/**
 * @author  lyc
 * @date    2021/1/19
 * @description:
 */
class PenWindowUtils {
    private var lowPowerWindowView: FrameLayout? = null
    private var bluetoothWindowView: FrameLayout? = null
    private var netErrorWindowView: LinearLayout? = null
    private var windowManager: WindowManager? = null

    private var lowPowerLP: WindowManager.LayoutParams? = null
    private var netErrorLP: WindowManager.LayoutParams? = null
    private var bluetoothLP: WindowManager.LayoutParams? = null

    private val bluetoothViewList: MutableList<View> = mutableListOf()
    private val penLowPowerViewList: MutableList<View> = mutableListOf()
    private val netErrorViewList: MutableList<View> = mutableListOf()


    constructor()

    constructor(application: Application) {
        windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    }

    public fun showPenLowPowerView(context: Context) {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        lowPowerWindowView =
            LayoutInflater.from(context).inflate(R.layout.low_power_layout, null) as FrameLayout?
        lowPowerLP = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        lowPowerLP!!.gravity = Gravity.RIGHT or Gravity.BOTTOM

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lowPowerLP!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            lowPowerLP!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        windowManager!!.addView(lowPowerWindowView, lowPowerLP)
        lowPowerWindowView?.let { penLowPowerViewList.add(it) }
    }

    public fun showNetErrorView(context: Context) {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        netErrorWindowView =
            LayoutInflater.from(context).inflate(R.layout.net_error_layout, null) as LinearLayout
        netErrorWindowView!!.findViewById<View>(R.id.net_error)
            .setOnClickListener(View.OnClickListener {
                if (windowManager != null && netErrorWindowView != null && netErrorWindowView!!.getWindowId() != null) {
                    windowManager!!.removeView(netErrorWindowView)
                }

                var intent: Intent
                if (Build.VERSION.SDK_INT > 10) {
                    intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                } else {
                    intent = Intent()
                    val componentName = ComponentName(
                        "com.android.settings",
                        "com.android.settings.WirelessSettings"
                    )
                    intent.component = componentName
                    intent.action = "android.intent.action.VIEW"
                }
                ActivityManagerUtil.getInstance()
            })
    }

    public fun showBluetoothView(context: Context) {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }

        bluetoothWindowView =
            LayoutInflater.from(context).inflate(R.layout.bluetooth_layout, null) as FrameLayout
        bluetoothWindowView!!.findViewById<View>(R.id.bluetooth_connection_img)
            .setOnClickListener {
                AppCommonUtils.getRefreshMusic()
                ActivityManagerUtil.getInstance().getTopActivity()?.startActivity(
                    Intent(
                        ActivityManagerUtil.getInstance().getTopActivity(),
                        BluetoothConnectionActivity::class.java
                    )
                )
                removeBluetoothWindow()
            }
        bluetoothLP = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        bluetoothLP!!.gravity = Gravity.RIGHT or Gravity.BOTTOM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bluetoothLP!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            bluetoothLP!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        windowManager!!.addView(bluetoothWindowView, bluetoothLP)
        bluetoothViewList.add(bluetoothWindowView!!)
    }

    fun removePenLowPowerWindow() {
        val iterator = penLowPowerViewList.iterator()
        while (iterator.hasNext()) {
            val view = iterator.next()
            if (windowManager != null && view != null) {
                windowManager!!.removeViewImmediate(view)
                iterator.remove()
            }
        }
    }

    fun removeNetErrorWindow() {
        val iterator = netErrorViewList.iterator()
        while (iterator.hasNext()) {
            val view = iterator.next()
            if (windowManager != null && view != null) {
                windowManager!!.removeViewImmediate(view)
                iterator.remove()
            }
        }
    }

    fun removeBluetoothWindow() {
        val iterator = bluetoothViewList.iterator()
        while (iterator.hasNext()) {
            val view = iterator.next()
            if (windowManager != null && view != null) {
                windowManager!!.removeViewImmediate(view)
                iterator.remove()
            }
        }
    }

}