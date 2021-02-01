package com.kotlin.smart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kotlin.smart.MyApplication
import com.kotlin.smart.R
import com.kotlin.smart.base.BaseActivity
import com.kotlin.smart.utils.PenWindowUtils

public class MainActivity : BaseActivity() {
    var isLoadedWeb: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startActivity(Intent(this, BluetoothConnectionActivity::class.java))

    }
}