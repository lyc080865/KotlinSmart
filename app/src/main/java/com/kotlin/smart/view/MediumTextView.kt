package com.kotlin.smart.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.kotlin.smart.MyApplication
import java.util.jar.Attributes

/**
 * @author  lyc
 * @date    2021/1/19
 * @description:
 */
@SuppressLint("AppCompatCustomView")
public class MediumTextView : TextView {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initView()
    }


    private fun initView() {
        typeface = MyApplication.mediumFont
        includeFontPadding = false
    }
}