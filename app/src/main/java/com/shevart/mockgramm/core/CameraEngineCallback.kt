package com.shevart.mockgramm.core

interface CameraEngineCallback {
    fun isCameraPermissionGranted(): Boolean

    fun requestCameraPermission()

    fun isStoragePermissionGranted(): Boolean

    fun requestStoragePermission()

    fun onCameraError(e: Throwable)

    fun cameraDevMessage(msg: String)

    fun getScreenRotation(): Int

    fun onShootPhotoStarted()

    fun onShootPhotoFinish()
}