package com.shevart.mockgramm.camera

interface CameraEngineCallback {
    fun isCameraPermissionGranted(): Boolean

    fun requestCameraPermission()
}