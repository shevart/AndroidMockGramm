package com.shevart.mockgramm

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.shevart.mockgramm.test.camera.TestCameraActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, TestCameraActivity::class.java))
    }
}
