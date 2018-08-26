package com.shevart.mockgramm.core.util

import com.shevart.mockgramm.core.camera.CameraEngine
import com.shevart.mockgramm.core.camera.Cameras

fun CameraEngine.changeCamera() {
    val currCamera = this.getCurrentCameraType()
    if (currCamera == Cameras.MAIN_CAMERA) {
        this.changeCamera(Cameras.SELFIE_CAMERA)
    } else {
        this.changeCamera(Cameras.MAIN_CAMERA)
    }
}