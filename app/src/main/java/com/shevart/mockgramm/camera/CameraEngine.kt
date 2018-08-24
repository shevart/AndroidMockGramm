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
import com.shevart.mockgramm.test.camera.TestCameraActivity
import kotlinx.android.synthetic.main.activity_test_camera.*
import java.util.*

@Suppress("unused")
class CameraEngine private constructor(
        private val textureView: TextureView,
        private val cameraEngineCallback: CameraEngineCallback
) : LifecycleObserver {
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraId: String? = null
    private var imageDimension: Size? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private val context: Context
        get() = textureView.context
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

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onHostCreated() {
        log("onHostCreated()")
        textureView.surfaceTextureListener = textureListener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onHostResumed() {
        log("onHostResumed()")
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = textureListener
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onHostPaused() {
        log("onHostPaused()")
        stopBackgroundThread()
    }

    private fun startCamera() {
        log("startCamera()")
        if (cameraEngineCallback.isCameraPermissionGranted()) {
            openCamera()
        } else {
            cameraEngineCallback.requestCameraPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        log("openCamera()")
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = manager.findMainCameraId()
            val characteristics = manager.getCameraCharacteristics(cameraId!!)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]

            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException) {
            cameraEngineCallback.onCameraError(e)
        }
    }

    private fun createCameraPreview() {
        log("createCameraPreview()")
        try {
            val texture = textureView.surfaceTexture!!
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder!!.addTarget(surface)
            cameraDevice!!.createCaptureSession(Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession
                    updatePreview()
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    showToast("onConfigureFailed - Configuration changed!")
                }
            }, null)
        } catch (e: CameraAccessException) {
            cameraEngineCallback.onCameraError(e)
        }
    }

    private fun updatePreview() {
        log("updatePreview()")
        if (cameraDevice == null) {
            log("updatePreview error, return")
            return
        }
        captureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions?.setRepeatingRequest(captureRequestBuilder!!.build(), null, backgroundHandler)
        } catch (e: CameraAccessException) {
            cameraEngineCallback.onCameraError(e)
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

    private fun log(msg: String) {
        Log.d("CameraEngine", msg)
        showToast(msg)
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