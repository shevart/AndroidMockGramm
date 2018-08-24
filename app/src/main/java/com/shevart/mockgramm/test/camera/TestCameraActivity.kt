package com.shevart.mockgramm.test.camera

import android.Manifest
import android.os.Bundle
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.camera.CameraEngine
import com.shevart.mockgramm.camera.CameraEngineCallback
import com.shevart.mockgramm.camera.Cameras
import com.shevart.mockgramm.util.CameraPermission
import kotlinx.android.synthetic.main.activity_test_camera.*

class TestCameraActivity : BaseActivity(), CameraEngineCallback {
    private lateinit var cameraEngine: CameraEngine

    override fun provideLayoutResId() = R.layout.activity_test_camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraEngine = CameraEngine
                .createCameraEngineInstance(textureView = txvCameraImage,
                        cameraEngineCallback = this,
                        lifecycleOwner = this)

        btTest.setOnClickListener {
            val currCamera = cameraEngine.getCurrentCameraType()
            if (currCamera == Cameras.MAIN_CAMERA) {
                cameraEngine.changeCamera(Cameras.SELFIE_CAMERA)
            } else {
                cameraEngine.changeCamera(Cameras.MAIN_CAMERA)
            }
        }
    }

    override fun isCameraPermissionGranted(): Boolean =
            CameraPermission.isGranted(this)

    override fun requestCameraPermission() {
        rxPermission
                .request(Manifest.permission.CAMERA)
                .subscribe(
                        this::onRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    override fun onCameraError(e: Throwable) {
        handleErrorDefault(e)
    }

    override fun cameraDevMessage(msg: String) {
        showToast(msg)
    }

    private fun onRequestPermissionResult(granted: Boolean) {
        if (granted) {
            showToast("RequestPermissionResult - granted!")
            // todo update camera?
//            openCamera()
        } else {
            showToast("There is no permission for work with camera!")
            finish()
        }
    }

    private fun onRequestPermissionError(e: Throwable) {
        handleErrorDefault(e)
        finish()
    }

    companion object {
        const val CAMERA_THREAD_NAME = "Camera Background"
    }
}
