@file:Suppress("unused")

package com.shevart.mockgramm.core.util

import android.hardware.camera2.*
import android.view.Surface

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

fun CameraDevice.createCaptureRequest(target: Surface, rotation: Int): CaptureRequest {
    val captureBuilder = this.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
    captureBuilder.apply {
        addTarget(target)
        set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        set(CaptureRequest.JPEG_ORIENTATION, rotation)
    }
    return captureBuilder.build()
}