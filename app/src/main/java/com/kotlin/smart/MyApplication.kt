package com.kotlin.smart

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.multidex.MultiDex
import com.kotlin.smart.activity.MainActivity
import com.kotlin.smart.service.MainBGMusicService
import com.kotlin.smart.utils.*
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.*
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader


/**
 * @author  lyc
 * @date    2021/1/19
 * @description:
 */
public class MyApplication : Application() {
    companion object {
        private var mInstance: MyApplication? = null
        var mContext: Context? = null
        var isOpenBgMusic: Boolean = true
        var isShowNetHint: Boolean = true
        var isShowPenLowPower: Boolean = true
        var isShowBluetooth: Boolean = true
        var isOnFront: Boolean = true
        var isConnectedPen: Boolean = true

        var mediumFont: Typeface? = null
        var boldFont: Typeface? = null
        var heavyFont: Typeface? = null
        var regularFont: Typeface? = null

        private var penWindowUtils: PenWindowUtils? = null

        var isClickedMovie: Boolean? = null

        fun getPenWindowUtils(): PenWindowUtils? {
            if (penWindowUtils == null) {
                penWindowUtils = PenWindowUtils()
            }
            return penWindowUtils
        }

        fun getInstance(): MyApplication? {
            if (null == mInstance) {
                mInstance = MyApplication()
            }
            return mInstance
        }

        private var musicService: MainBGMusicService? = null

        init {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator(object : DefaultRefreshHeaderCreator {
                override fun createRefreshHeader(
                    context: Context,
                    layout: RefreshLayout
                ): RefreshHeader {
                    layout.setPrimaryColorsId(R.color.white, R.color.color_885400)
                    return ClassicsHeader(context).setFinishDuration(0)
                }
            })

            SmartRefreshLayout.setDefaultRefreshFooterCreator(object : DefaultRefreshFooterCreator {
                override fun createRefreshFooter(
                    context: Context,
                    layout: RefreshLayout
                ): RefreshFooter {
                    return ClassicsFooter(context).setDrawableSize(20F).setFinishDuration(0)
                }
            })

        }

    }


    override fun onCreate() {
        super.onCreate()

        isShowNetHint = true
        penWindowUtils = PenWindowUtils()

        mediumFont = Typeface.createFromAsset(assets, "ResourceHanRoundedCN-Medium.ttf")
        boldFont = Typeface.createFromAsset(assets, "fonts/ResourceHanRoundedCN-Bold.ttf")
        heavyFont = Typeface.createFromAsset(assets, "fonts/ResourceHanRoundedCN-Heavy.ttf")
        regularFont = Typeface.createFromAsset(assets, "fonts/ResourceHanRoundedCN-Regular.ttf")

        mContext = applicationContext
        DataStoreUtils.init(this)

        //TODO  蓝牙、友盟、bugly未集成
        val intent = Intent()
        intent.setClass(this, MainBGMusicService::class.java)
        coon = MusicConnection();
        bindService(intent, coon!!, Context.BIND_AUTO_CREATE)

        val helper = AppFrontBackHelper()
        helper.register(this, object : AppFrontBackHelper.OnAppStatusListener {
            override fun onFront() {
                isOnFront = true
                muteAudio(true)
                if (isShowPenLowPower) {
                    ActivityManagerUtil.getInstance().getTopActivity()?.showPenLowPowerWindow()
                }

                if (NetUtil.isNetDisConnection()) {
                    ActivityManagerUtil.getInstance().getTopActivity()?.showNetErrorWindow()
                }

                if (!isSpecialActivity(ActivityManagerUtil.getInstance().getTopActivity()!!)) {
                    startBGMusic()
                }

            }

            override fun onBack() {
                //应用切到后台处理
                isOnFront = false
                pauseBGMusic()
                ActivityManagerUtil.getInstance().getTopActivity()?.disPenLowPowerWindow()
                ActivityManagerUtil.getInstance().getTopActivity()?.disNetErrorWindow()
                ActivityManagerUtil.getInstance().getTopActivity()?.disBluetoothWindow()
                val activity: MainActivity? = ActivityManagerUtil.getInstance().getActivity(
                    MainActivity::class.java
                ) as MainActivity?
                if (activity != null) {
                    activity.isLoadedWeb = false
                }
            }
        })

    }

    override fun onTerminate() {
        super.onTerminate()
        coon?.let { unbindService(it) }
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private var coon: MusicConnection? = null

    private class MusicConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MainBGMusicService.MusicBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun pauseBGMusic() {
        musicService?.pauseBgMusic()
    }

    fun startBGMusic() {
        musicService?.startBgMusic()
    }

    final var mOnAudioFocusChangeListener = object : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {

        }
    }

    fun muteAudio(bMute: Boolean): Boolean {
        var isSuccess = false;
        var am = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (bMute) {
            val result = am.requestAudioFocus(
                mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            isSuccess = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            var result = 0
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                result = am.abandonAudioFocusRequest(null)
//            }
            isSuccess = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
        return isSuccess
    }

    fun isSpecialActivity(activity: Activity): Boolean {
        return false
    }
}