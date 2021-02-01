package com.kotlin.smart.utils

import android.content.Context
import android.content.SharedPreferences
import com.kotlin.smart.MyApplication
import com.kotlin.smart.R

/**
 * @author  lyc
 * @date    2021/1/21
 * @description:
 */
class DataStoreUtils {
    companion object {
        var context: Context? = null

        fun init(context: Context) {
            DataStoreUtils.context = context;
        }

        fun getBoolean(key: String, values: Boolean): Boolean{
            return getSharePreference().getBoolean(key, values)
        }

        fun getSharePreference(): SharedPreferences {
            return context!!.getSharedPreferences(
                MyApplication.mContext!!.getString(R.string.app_name),
                Context.MODE_PRIVATE
            ) as SharedPreferences
        }
    }
}