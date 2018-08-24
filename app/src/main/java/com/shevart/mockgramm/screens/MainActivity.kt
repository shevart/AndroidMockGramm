package com.shevart.mockgramm.screens

import android.content.Intent
import android.os.Bundle
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.screens.camera.CameraFragment
import com.shevart.mockgramm.test.camera.TestCameraActivity

class MainActivity : BaseActivity() {
    override fun provideLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportFragmentManager.findFragmentById(R.id.flContainer) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.flContainer, CameraFragment.getInstance())
                    .commit()
        }

        // todo test only code
//        startActivity(Intent(this, TestCameraActivity::class.java))
    }
}
