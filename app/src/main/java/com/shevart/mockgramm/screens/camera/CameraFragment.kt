package com.shevart.mockgramm.screens.camera

import android.Manifest
import android.os.Bundle
import android.view.View
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseFragment
import com.shevart.mockgramm.core.camera.CameraEngine
import com.shevart.mockgramm.core.camera.CameraEngineCallback
import com.shevart.mockgramm.core.util.changeCamera
import com.shevart.mockgramm.core.util.provideScreenRotation
import com.shevart.mockgramm.util.*
import kotlinx.android.synthetic.main.activity_test_camera.*
import kotlinx.android.synthetic.main.layout_camera_dashboard.*
import java.io.File

class CameraFragment : BaseFragment(), CameraEngineCallback {
    private lateinit var cameraEngine: CameraEngine

    override fun provideLayoutResId() = R.layout.fragment_camera

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraEngine = CameraEngine.createCameraEngineInstance(
                textureView = txvCameraImage,
                cameraEngineCallback = this,
                lifecycleOwner = this)

        ibShoot.setOnClickListener { cameraEngine.shootPhoto() }
        ivChangeCamera.setOnClickListener { cameraEngine.changeCamera() }
    }

    override fun getScreenRotation() = provideScreenRotation()

    override fun isCameraPermissionGranted() =
            CameraPermission.isGranted(forceContext)

    override fun requestCameraPermission() {
        rxPermission
                .request(Manifest.permission.CAMERA)
                .subscribe(
                        this::onCameraRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    override fun isStoragePermissionGranted() =
            WriteStoragePermission.isGranted(forceContext)

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

    override fun onShootPhotoStarted() {
        pbShootingProgress.visible()
        ibShoot.disable()
    }

    override fun onShootPhotoFinish() {
        pbShootingProgress.gone()
        ibShoot.enable()
    }

    override fun createFileForNextPhoto(): File {
        return createPhotoFile("/pic.jpg")
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
        showToast(R.string.error_no_camera_permission)
        if (withFinish) {
            activity?.finish()
        }
    }

    private fun onRequestPermissionError(e: Throwable) {
        handleErrorDefault(e)
    }

    companion object {
        fun getInstance() = CameraFragment()
    }
}