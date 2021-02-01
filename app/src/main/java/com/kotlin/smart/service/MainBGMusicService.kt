package com.kotlin.smart.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.kotlin.smart.R
import com.kotlin.smart.constant.Constants
import com.kotlin.smart.utils.DataStoreUtils

/**
 * @author  lyc
 * @date    2021/1/21
 * @description:
 */
public class MainBGMusicService : Service(), MediaPlayer.OnCompletionListener {
    private lateinit var mediaPlayer: MediaPlayer
    private var musicBinder: MusicBinder = MusicBinder()

    fun pauseBgMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun startBgMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying) {
            if (DataStoreUtils.getBoolean(Constants.SP_MUSIC_SWITCH, true)) {
                mediaPlayer.start()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.all_bg_music)
        mediaPlayer.setOnCompletionListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!mediaPlayer.isPlaying) {
            if (DataStoreUtils.getBoolean(Constants.SP_MUSIC_SWITCH, true)) {
                //开始播放
                mediaPlayer.start()
            }
            mediaPlayer.isLooping = true
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (!mediaPlayer.isPlaying) {
            if (DataStoreUtils.getBoolean(Constants.SP_MUSIC_SWITCH, true)) {
                //开始播放
                mediaPlayer.start()
            }
            mediaPlayer.isLooping = true
        }
        return musicBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopSelf()
    }

    inner class MusicBinder : Binder() {
        //返回Service对象
        val service: MainBGMusicService
            get() = this@MainBGMusicService
    }
}