@file:Suppress("unused")

package com.shevart.mockgramm.camera.util

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

fun CameraManager.findMainCameraId(): String? {
    return this.findCameraIdByLensFacing(CameraCharacteristics.LENS_FACING_BACK)
}

fun CameraManager.findSelfieCameraId(): String? {
    return this.findCameraIdByLensFacing(CameraCharacteristics.LENS_FACING_FRONT)
}

fun CameraManager.findCameraIdByLensFacing(cameraFacing: Int): String? {
    for (cameraId in this.cameraIdList.toList()) {
        val characteristics = this.getCameraCharacteristics(cameraId)
        if (characteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
            return cameraId
        }
    }
    return null
}