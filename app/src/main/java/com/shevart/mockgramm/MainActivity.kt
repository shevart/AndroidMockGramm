package com.shevart.mockgramm

import android.content.Intent
import android.os.Bundle
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.test.camera.TestCameraActivity

class MainActivity : BaseActivity() {
    override fun provideLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        // todo test only code
        startActivity(Intent(this, TestCameraActivity::class.java))
    }
}
