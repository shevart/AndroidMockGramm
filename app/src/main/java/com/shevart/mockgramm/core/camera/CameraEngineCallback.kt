package com.shevart.mockgramm.core.camera

import java.io.File

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

    fun createFileForNextPhoto(): File

    fun onPhotoSaved(file: File)
}