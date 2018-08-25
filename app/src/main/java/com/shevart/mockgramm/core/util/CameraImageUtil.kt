package com.shevart.mockgramm.core.util

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import com.shevart.mockgramm.core.camera.CameraConfig
import com.shevart.mockgramm.util.save
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

fun CameraCharacteristics.getImageSizes(): Pair<Int, Int> {
    val jpegSizes = this.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            ?.getOutputSizes(ImageFormat.JPEG)
    val photoWidth = jpegSizes?.getOrNull(0)?.width ?: CameraConfig.DEFAULT_WIDTH
    val photoHeight = jpegSizes?.getOrNull(1)?.height ?: CameraConfig.DEFAULT_HEIGHT

    return Pair(photoWidth, photoHeight)
}

fun CameraManager.createImageReader(cameraDevice: CameraDevice): ImageReader {
    val characteristics = this.getCameraCharacteristics(cameraDevice.id)
    val imageSizes = characteristics.getImageSizes()
    return ImageReader.newInstance(imageSizes.first, imageSizes.second, ImageFormat.JPEG, 1)
}

fun ImageReader.saveImageToFile(file: File) {
    var image: Image? = null
    try {
        image = this.acquireLatestImage()
        val buffer = image!!.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        file.save(bytes)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        image?.close()
    }
}