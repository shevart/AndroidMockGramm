package com.shevart.mockgramm.camera

interface CameraEngineCallback {
    fun isCameraPermissionGranted(): Boolean

    fun requestCameraPermission()

    fun onCameraError(e: Throwable)

    fun cameraDevMessage(msg: String)
}