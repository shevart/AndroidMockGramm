package com.shevart.mockgramm.camera

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.TextureView
import com.shevart.mockgramm.camera.util.EmptyTextureSurfaceListener

@Suppress("unused")
class CameraEngine private constructor(private val textureView: TextureView) : LifecycleObserver {
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraId: String? = null
    private var imageDimension: Size? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private val textureListener: TextureView.SurfaceTextureListener = object : EmptyTextureSurfaceListener() {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            startCamera()
        }
    }
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            //This is called when the camera is open

            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    fun startCamera() {

    }

    fun createCameraPreview() {

    }

    companion object {
        fun createCameraEngineInstance(textureView: TextureView,
                                       lifecycleOwner: LifecycleOwner): CameraEngine {
            val cameraEngine = CameraEngine(textureView)
            lifecycleOwner.lifecycle.addObserver(cameraEngine)
            return cameraEngine
        }
    }
}