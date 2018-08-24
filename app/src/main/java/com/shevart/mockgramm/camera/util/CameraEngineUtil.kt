package com.shevart.mockgramm.camera.util

import com.shevart.mockgramm.camera.CameraEngine
import com.shevart.mockgramm.camera.Cameras

fun CameraEngine.changeCamera() {
    val currCamera = this.getCurrentCameraType()
    if (currCamera == Cameras.MAIN_CAMERA) {
        this.changeCamera(Cameras.SELFIE_CAMERA)
    } else {
        this.changeCamera(Cameras.MAIN_CAMERA)
    }
}