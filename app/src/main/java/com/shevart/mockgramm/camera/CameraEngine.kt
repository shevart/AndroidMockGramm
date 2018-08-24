package com.shevart.mockgramm.camera

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.shevart.mockgramm.camera.util.EmptyTextureSurfaceListener
import com.shevart.mockgramm.camera.util.findMainCameraId
import com.shevart.mockgramm.camera.util.findSelfieCameraId
import com.shevart.mockgramm.test.camera.TestCameraActivity
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Suppress("unused")
class CameraEngine private constructor(
        private val textureView: TextureView,
        private val cameraEngineCallback: CameraEngineCallback
) : LifecycleObserver {
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private var cameraDevice: CameraDevice? = null
    private var imageDimension: Size? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null

    private val reentrantLock = ReentrantLock()
    private var currentCamera = Cameras.MAIN_CAMERA

    private val textureListener: TextureView.SurfaceTextureListener = object : EmptyTextureSurfaceListener() {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            startCamera()
        }
    }
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            onCameraOpened()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraEngineCallback.cameraDevMessage("stateCallback - onDisconnected")
            closeCamera()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraEngineCallback.cameraDevMessage("stateCallback - onError, error=$error")
            closeCamera()
        }
    }
    private val context: Context
        get() = textureView.context

    fun changeCamera(camera: Cameras) {
        if (currentCamera != camera) {
            stopCameraEngine()
            currentCamera = camera
            startCameraEngine()
        }
    }

    fun getCurrentCameraType() = currentCamera

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onHostCreated() {
        textureView.surfaceTextureListener = textureListener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onHostResumed() {
        startCameraEngine()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onHostPaused() {
        stopCameraEngine()
    }

    private fun startCameraEngine() {
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = textureListener
        }
    }

    private fun startCamera() {
        if (cameraEngineCallback.isCameraPermissionGranted()) {
            openCamera()
        } else {
            cameraEngineCallback.requestCameraPermission()
        }
    }

    private fun stopCameraEngine() {
        closeCamera()
        stopBackgroundThread()
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = findCurrentCameraId(manager)
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            manager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            cameraEngineCallback.onCameraError(e)
        } catch (e: IllegalAccessError) {
            cameraEngineCallback.onCameraError(e)
        }
    }

    private fun findCurrentCameraId(manager: CameraManager): String {
        val cameraId = when (currentCamera) {
            Cameras.MAIN_CAMERA -> manager.findMainCameraId()
            Cameras.SELFIE_CAMERA -> manager.findSelfieCameraId()
        }
        return cameraId ?: throw IllegalAccessError("The camera not found!")
    }


    private fun closeCamera() {
        runThreadSafely { cameraDevice?.close() }
    }

    private fun onCameraOpened() {
        val size = imageDimension
                ?: throw IllegalStateException("You must init imageDimension!")
        val camera = cameraDevice
                ?: throw IllegalStateException("You must open cameraDevice!")
        createCameraPreview(camera, size)
    }

    private fun createCameraPreview(cameraDevice: CameraDevice, imageDimension: Size) {
        log("createCameraPreview()")
        try {
            val surface = createSurface(imageDimension)
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder!!.addTarget(surface)
            cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    if (isCameraOpened()) {
                        cameraCaptureSessions = cameraCaptureSession
                        updatePreview()
                    }
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    showToast("onConfigureFailed - Configuration changed!")
                }
            }, null)
        } catch (e: CameraAccessException) {
            cameraEngineCallback.onCameraError(e)
        }
    }

    private fun isCameraOpened() = cameraDevice != null

    private fun updatePreview() {
        if (isCameraOpened()) {
            captureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            try {
                cameraCaptureSessions?.setRepeatingRequest(captureRequestBuilder!!.build(), null, backgroundHandler)
            } catch (e: CameraAccessException) {
                cameraEngineCallback.onCameraError(e)
            }
        } else {
            log("updatePreview() error - camera is closed!")
        }
    }

    private fun startBackgroundThread() {
        log("startBackgroundThread()")
        backgroundThread = HandlerThread(TestCameraActivity.CAMERA_THREAD_NAME)
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        log("stopBackgroundThread()")
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            cameraEngineCallback.onCameraError(e)
        }
    }

    private fun createSurface(imageDimension: Size): Surface {
        val texture = textureView.surfaceTexture!!
        texture.setDefaultBufferSize(imageDimension.width, imageDimension.height)
        return Surface(texture)
    }

    private fun runThreadSafely(action: () -> Unit) {
        try {
            reentrantLock.withLock { action() }
        } catch (e: InterruptedException) {
            cameraEngineCallback.onCameraError(e)
            throw RuntimeException("Interrupted!")
        }
    }

    private fun log(msg: String, toast: Boolean = true) {
        Log.d("CameraEngine", msg)
        if (toast) {
            showToast(msg)
        }
    }

    private fun showToast(msg: String) =
            cameraEngineCallback.cameraDevMessage(msg)

    companion object {
        fun createCameraEngineInstance(textureView: TextureView,
                                       cameraEngineCallback: CameraEngineCallback,
                                       lifecycleOwner: LifecycleOwner): CameraEngine {
            val cameraEngine = CameraEngine(textureView, cameraEngineCallback)
            lifecycleOwner.lifecycle.addObserver(cameraEngine)
            return cameraEngine
        }
    }
}