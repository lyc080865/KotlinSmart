package com.kotlin.smart.utils

import android.content.Context
import com.kotlin.smart.MyApplication

/**
 * @author  lyc
 * @date    2021/1/26
 * @description:
 */
public class NetUtil {
    companion object {
        /**
         * 没有连接网络
         */
        const val NETWORK_NONE = -1;

        /**
         * 移动网络
         */
        const val NETWORK_MOBILE = 0;

        /**
         * 无线网络
         */
        const val NETWORK_WIFI = 1;

        fun getNetWorkState(context: Context): Int {


            return NETWORK_NONE
        }

        fun isNetDisConnection(): Boolean {
            return NETWORK_NONE == MyApplication.mContext?.let { getNetWorkState(it) }
        }
    }
}