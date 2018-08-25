package com.shevart.mockgramm.core.camera

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.shevart.mockgramm.core.util.*
import java.io.File
import java.util.ArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Suppress("unused", "PrivatePropertyName")
// todo refactor - should be more readable
// todo add comment
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
    private val handler = Handler(Looper.getMainLooper())

    private val textureListener = object : EmptyTextureSurfaceListener() {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            startCameraEngine()
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
    private val cameraManager: CameraManager
        get() = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraPermissionGranted: Boolean
        get() = cameraEngineCallback.isCameraPermissionGranted()
    private val storagePermissionGranted: Boolean
        get() = cameraEngineCallback.isStoragePermissionGranted()

    fun changeCamera(camera: Cameras) {
        if (currentCamera != camera) {
            stopCameraEngine()
            currentCamera = camera
            startCameraEngine()
        }
    }

    fun getCurrentCameraType() = currentCamera

    fun shootPhoto() {
        if (storagePermissionGranted) {
            val camera = cameraDevice
                    ?: throw IllegalStateException("You must open cameraDevice!")
            synchronized(this) {
                takePicture(camera)
            }
        } else {
            cameraEngineCallback.requestStoragePermission()
        }
    }

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
            startCamera()
        } else {
            textureView.surfaceTextureListener = textureListener
        }
    }

    private fun startCamera() {
        if (cameraPermissionGranted) {
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
                    showDevToast("onConfigureFailed - Configuration changed!")
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
                cameraCaptureSessions?.setRepeatingRequest(
                        captureRequestBuilder!!.build(), null, backgroundHandler)
            } catch (e: CameraAccessException) {
                cameraEngineCallback.onCameraError(e)
            }
        } else {
            log("updatePreview() error - camera is closed!")
        }
    }

    private fun startBackgroundThread() {
        log("startBackgroundThread()")
        backgroundThread = HandlerThread(CAMERA_THREAD_NAME)
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

    // todo refactor!
    private fun takePicture(cameraDevice: CameraDevice) {
        if (!isCameraOpened()) {
            log("cameraDevice is null!")
            return
        }
        try {
            // Create image reader
            val reader = cameraManager.createImageReader(cameraDevice)
            val outputSurfaces = ArrayList<Surface>(2)
            outputSurfaces.add(reader.surface)
            outputSurfaces.add(Surface(textureView.surfaceTexture))
            val rotation = ORIENTATIONS.get(cameraEngineCallback.getScreenRotation())
            val captureRequest = cameraDevice.createCaptureRequest(reader.surface, rotation)

            // Image file
            val file = cameraEngineCallback.createFileForNextPhoto()
            val readerListener = ImageReader.OnImageAvailableListener { imageReader ->
                imageReader.saveImageToFile(file)
            }

            reader.setOnImageAvailableListener(readerListener, backgroundHandler)
            val captureListener = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest,
                                                result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    onPhotoSaved(file)
                    onCameraShootPhotoFinished()
                    onCameraOpened()
                }
            }
            cameraDevice.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    try {
                        onCameraShootPhotoStarted()
                        session.capture(captureRequest, captureListener, backgroundHandler)
                    } catch (e: CameraAccessException) {
                        onCameraShootPhotoFailed(e)
                    } catch (another: Exception) {
                        onCameraShootPhotoFailed(another)
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    cameraEngineCallback.onShootPhotoFinish()
                }
            }, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun onCameraShootPhotoStarted() {
        runOnMainThread { cameraEngineCallback.onShootPhotoStarted() }
    }

    private fun onCameraShootPhotoFinished() {
        runOnMainThread { cameraEngineCallback.onShootPhotoFinish() }
    }

    private fun onPhotoSaved(photoFile: File) {
        runOnMainThread {
            showDevToast("Saved: $photoFile")
            cameraEngineCallback.onPhotoSaved(photoFile)
        }
    }

    private fun onCameraShootPhotoFailed(e: Exception) {
        runOnMainThread {
            cameraEngineCallback.onCameraError(e)
            cameraEngineCallback.onShootPhotoFinish()
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

    private fun runOnMainThread(action: () -> Unit) {
        handler.post { action() }
    }

    private fun log(msg: String) {
        Log.d("CameraEngine", msg)
    }

    private fun showDevToast(msg: String) =
            cameraEngineCallback.cameraDevMessage(msg)

    companion object {
        const val CAMERA_THREAD_NAME = "Camera Background"

        fun createCameraEngineInstance(textureView: TextureView,
                                       cameraEngineCallback: CameraEngineCallback,
                                       lifecycleOwner: LifecycleOwner): CameraEngine {
            val cameraEngine = CameraEngine(textureView, cameraEngineCallback)
            lifecycleOwner.lifecycle.addObserver(cameraEngine)
            return cameraEngine
        }
    }
}