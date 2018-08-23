package com.shevart.mockgramm.test.camera

import android.Manifest
import android.os.Bundle
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity

class TestCameraActivity : BaseActivity() {
    override fun provideLayoutResId() = R.layout.activity_test_camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxPermission
                .request(Manifest.permission.CAMERA)
                .subscribe(
                        this::onRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    private fun onRequestPermissionResult(granted: Boolean) {
        if (!granted) {
            showToast("There is no permission for work with camera!")
            finish()
        }
    }

    private fun onRequestPermissionError(e: Throwable) {
        showToast(e.localizedMessage)
        e.printStackTrace()
    }
}
