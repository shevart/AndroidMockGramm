package com.shevart.mockgramm.test.camera

import android.Manifest
import android.os.Bundle
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.camera.CameraEngine
import com.shevart.mockgramm.camera.CameraEngineCallback
import com.shevart.mockgramm.camera.Cameras
import com.shevart.mockgramm.camera.util.provideScreenRotation
import com.shevart.mockgramm.util.CameraPermission
import com.shevart.mockgramm.util.WriteStoragePermission
import kotlinx.android.synthetic.main.activity_test_camera.*

// todo remove str hardcodes
class TestCameraActivity : BaseActivity(), CameraEngineCallback {
    private lateinit var cameraEngine: CameraEngine


    override fun provideLayoutResId() = R.layout.activity_test_camera

    override fun getScreenRotation() = this.provideScreenRotation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraEngine = CameraEngine
                .createCameraEngineInstance(textureView = txvCameraImage,
                        cameraEngineCallback = this,
                        lifecycleOwner = this)

        btTest.setOnClickListener {
            cameraEngine.shootPhoto()
        }
    }

    override fun isCameraPermissionGranted(): Boolean =
            CameraPermission.isGranted(this)

    override fun requestCameraPermission() {
        rxPermission
                .request(Manifest.permission.CAMERA)
                .subscribe(
                        this::onCameraRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    override fun isStoragePermissionGranted() =
            WriteStoragePermission.isGranted(this)

    override fun requestStoragePermission() {
        rxPermission
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(
                        this::onWriteStorageRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    override fun onCameraError(e: Throwable) {
        handleErrorDefault(e)
    }

    override fun cameraDevMessage(msg: String) {
        showToast(msg)
    }

    // todo update camera?
    private fun onCameraRequestPermissionResult(granted: Boolean) {
        if (!granted) {
            onPermissionNotGranted()
        }
    }

    // todo update camera?
    private fun onWriteStorageRequestPermissionResult(granted: Boolean) {
        if (!granted) {
            onPermissionNotGranted()
        }
    }

    private fun onPermissionNotGranted(withFinish: Boolean = true) {
        showToast("There is no permission for work with camera!")
        if (withFinish) {
            finish()
        }
    }

    private fun onRequestPermissionError(e: Throwable) {
        handleErrorDefault(e)
    }

    private fun changeCamera() {
        val currCamera = cameraEngine.getCurrentCameraType()
        if (currCamera == Cameras.MAIN_CAMERA) {
            cameraEngine.changeCamera(Cameras.SELFIE_CAMERA)
        } else {
            cameraEngine.changeCamera(Cameras.MAIN_CAMERA)
        }
    }

    companion object {
        const val CAMERA_THREAD_NAME = "Camera Background"
    }
}
