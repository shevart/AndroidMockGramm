package com.shevart.mockgramm.screens

import android.os.Bundle
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.screens.camera.CameraFragment

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
    }
}
