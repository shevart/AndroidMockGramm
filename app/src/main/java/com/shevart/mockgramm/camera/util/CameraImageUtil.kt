package com.shevart.mockgramm.camera.util

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import com.shevart.mockgramm.camera.CameraConfig

fun CameraCharacteristics.getImageSizes(): Pair<Int, Int> {
    val jpegSizes = this.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            ?.getOutputSizes(ImageFormat.JPEG)
    val photoWidth = jpegSizes?.getOrNull(0)?.width ?: CameraConfig.DEFAULT_WIDTH
    val photoHeight = jpegSizes?.getOrNull(1)?.height ?: CameraConfig.DEFAULT_HEIGHT

//        var width = CameraConfig.DEFAULT_WIDTH
//        var height = CameraConfig.DEFAULT_HEIGHT
//        if (jpegSizes != null && jpegSizes.isNotEmpty()) {
//            width = jpegSizes[0].width
//            height = jpegSizes[0].height
//        }

    return Pair(photoWidth, photoHeight)
}

fun CameraManager.createImageReader(cameraDevice: CameraDevice): ImageReader {
    val characteristics = this.getCameraCharacteristics(cameraDevice.id)
    val imageSizes = characteristics.getImageSizes()
    return ImageReader.newInstance(imageSizes.first, imageSizes.second, ImageFormat.JPEG, 1)
}