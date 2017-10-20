package com.example.dmx.customviewdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.R.attr.button
import android.view.View


class MainActivity : AppCompatActivity() {

    var button: Button? = null
    var sumView: SunAnimationView? = null

    private var mCurrentTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        mCurrentTime = "15:40"
        sumView = findViewById(R.id.sun_view)
        button = findViewById(R.id.btn_set_time)
        button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                sumView?.setTimes("05:10", "18:40", mCurrentTime!!)
                button?.setText("当前时间：" + mCurrentTime)
            }
        })
    }
}
