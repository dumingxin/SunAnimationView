package com.example.dmx.customviewdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mCurrentTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        mCurrentTime = "15:40"
        btnSetTime?.setOnClickListener {
            sunView?.setTimes("05:10", "18:40", mCurrentTime!!)
            btnSetTime?.text = "当前时间：" + mCurrentTime
        }
    }
}
