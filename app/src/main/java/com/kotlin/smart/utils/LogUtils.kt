package com.kotlin.smart.utils

import android.util.Log
import com.kotlin.smart.constant.Constants

/**
 * @author  lyc
 * @date    2021/1/19
 * @description:
 */
class LogUtils {
    companion object {
        /**
         * 日志输出时的TAG
         */
        const val mTag: String = "LPT-"

        /**
         * 日志输出级别NONE
         */
        const val LEVEL_OFF = 0

        /**
         * 日志输出级别ALL
         */
        const val LEVEL_ALL = 7

        /**
         * 日志输出级别V
         */
        const val LEVEL_VERBOSE = 1

        /**
         * 日志输出级别D
         */
        const val LEVEL_DEBUG = 2

        /**
         * 日志输出级别I
         */
        const val LEVEL_INFO = 3

        /**
         * 日志输出级别W
         */
        const val LEVEL_WARN = 4

        /**
         * 日志输出级别E
         */
        const val LEVEL_ERROR = 5

        /**
         * 日志输出级别S,自定义定义的一个级别
         */
        const val LEVEL_SYSTEM = 6

        /**
         * 是否允许输出log
         */
        private const val mDebuggable = LEVEL_ALL

        /**
         * 用于记时的变量
         */
        private var mTimestamp: Long = 0

        /**
         * 写文件的锁对象
         */
        private val mLongLock = Any();

        /**
         * 以级别为 v 的形式输出LOG
         */
        fun v(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_VERBOSE) {
                Log.v(mTag, msg)
            }
        }

        /**
         * 以级别为 d 的形式输出LOG
         */
        fun d(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_DEBUG) {
                Log.d(mTag, msg)
            }
        }

        /**
         * 以级别为 i 的形式输出LOG
         */
        fun i(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_INFO) {
                Log.i(mTag, msg)
            }
        }

        /**
         * 以级别为 w 的形式输出LOG
         */
        fun w(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_WARN) {
                Log.w(mTag, msg)
            }
        }

        /**
         * 以级别为 w 的形式输出Throwable
         */
        fun w(tr: Throwable?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (mDebuggable >= LEVEL_WARN) {
                Log.w(mTag, "", tr)
            }
        }

        /**
         * 以级别为 w 的形式输出LOG信息和Throwable
         */
        fun w(msg: String?, tr: Throwable?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_WARN && null != msg) {
                Log.w(mTag, msg, tr)
            }
        }

        /**
         * 以级别为 e 的形式输出LOG
         */
        fun e(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_ERROR) {
                Log.e(mTag, msg)
            }
        }

        /**
         * 以级别为 s 的形式输出LOG,主要是为了System.out.println,稍微格式化了一下
         */
        fun sf(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_ERROR) {
                println("----------$msg----------")
            }
        }

        /**
         * 以级别为 s 的形式输出LOG,主要是为了System.out.println
         */
        fun s(msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_ERROR) {
                println(msg)
            }
        }

        /**
         * 以级别为 e 的形式输出Throwable
         */
        fun e(tr: Throwable?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (mDebuggable >= LEVEL_ERROR) {
                Log.e(mTag, "", tr)
            }
        }

        /**
         * 以级别为 e 的形式输出LOG信息和Throwable
         */
        fun e(msg: String?, tr: Throwable?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_ERROR && null != msg) {
                Log.e(mTag, msg, tr)
            }
        }

        /**---------------日志输出,未固定TAG  begin---------------**/
        /**---------------日志输出,已固定TAG  end--------------- */
        /**---------------日志输出,未固定TAG  begin--------------- */
        /**
         * 以级别为 d 的形式输出LOG
         */
        fun v(tag: String?, msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_VERBOSE) {
                Log.v(tag, msg)
            }
        }

        /**
         * 以级别为 d 的形式输出LOG
         */
        fun d(tag: String?, msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_DEBUG) {
                Log.d(tag, msg)
            }
        }

        /**
         * 以级别为 i 的形式输出LOG
         */
        fun i(tag: String?, msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_INFO) {
                Log.i(tag, msg)
            }
        }

        /**
         * 以级别为 w 的形式输出LOG
         */
        fun w(tag: String?, msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_WARN) {
                Log.w(tag, msg)
            }
        }

        /**
         * 以级别为 e 的形式输出LOG
         */
        fun e(tag: String?, msg: String?) {
            if (!Constants.APP_BUILD_MODE_DEBUG) {
                return
            }
            if (null == msg) {
                return
            }
            if (mDebuggable >= LEVEL_ERROR) {
                Log.e(tag, msg)
            }
        }
        /**---------------日志输出,未固定TAG  end---------------**/

        /**---------------日志输出,未固定TAG  end--------------- */
        /**
         * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段起始点
         *
         * @param msg 需要输出的msg
         */
        fun msgStartTime(msg: String) {
            mTimestamp = System.currentTimeMillis()
            if (!msg.isNullOrEmpty()) {
                e("[Started: $mTimestamp]$msg")
            }
        }

        /**
         * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg
         */
        fun elapsed(msg: String) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - mTimestamp
            mTimestamp = currentTime
            e("[Elapsed：$elapsedTime]$msg")
        }

        fun <T> printList(list: List<T>?) {
            if (list == null || list.size < 1) {
                return
            }
            val size = list.size
            i("---begin---")
            for (i in 0 until size) {
                i(i.toString() + ":" + list[i].toString())
            }
            i("---end---")
        }

        fun <T> printArray(array: Array<T>?) {
            if (array == null || array.size < 1) {
                return
            }
            val length = array.size
            i("---begin---")
            for (i in 0 until length) {
                i(i.toString() + ":" + array[i].toString())
            }
            i("---end---")
        }
    }
}