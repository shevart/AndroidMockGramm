package com.shevart.mockgramm.test.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.camera.CameraEngineCallback
import com.shevart.mockgramm.camera.util.EmptyTextureSurfaceListener
import com.shevart.mockgramm.util.CameraPermission
import kotlinx.android.synthetic.main.activity_test_camera.*
import java.util.*

class TestCameraActivity : BaseActivity(), CameraEngineCallback {
    // Camera related fields
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

    override fun provideLayoutResId() = R.layout.activity_test_camera



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        txvCameraImage.surfaceTextureListener = textureListener
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (txvCameraImage.isAvailable) {
            openCamera()
        } else {
            txvCameraImage.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        super.onPause()
        stopBackgroundThread()
    }

    override fun isCameraPermissionGranted(): Boolean =
            CameraPermission.isNeedRequest(this)

    override fun requestCameraPermission() {
        rxPermission
                .request(Manifest.permission.CAMERA)
                .subscribe(
                        this::onRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    private fun startCamera() {
        rxPermission
                .request(Manifest.permission.CAMERA)
                .subscribe(
                        this::onRequestPermissionResult,
                        this::onRequestPermissionError)
    }

    private fun onRequestPermissionResult(granted: Boolean) {
        if (granted) {
            showToast("RequestPermissionResult - granted!")
//            openCamera()
        } else {
            showToast("There is no permission for work with camera!")
            finish()
        }
    }

    private fun onRequestPermissionError(e: Throwable) {
        handleErrorDefault(e)
        finish()
    }

    @SuppressLint("MissingPermission")
    // todo - check is permission granted?
    // todo - use only back camera
    private fun openCamera() {
        log("openCamera()")
        showToast("openCamera()")
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = manager.findMainCameraId()
            val characteristics = manager.getCameraCharacteristics(cameraId!!)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]

            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun CameraManager.findMainCameraId(): String? {
        for (cameraId in this.cameraIdList.toList()) {
            val characteristics = this.getCameraCharacteristics(cameraId)
            val mainCamera = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            log("CAMERA_ID=$cameraId, mainCamera=$mainCamera")
            if (mainCamera) {
                return cameraId
            }
        }
        return null
    }




    private fun createCameraPreview() {
        try {
            val texture = txvCameraImage.surfaceTexture!!
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
            e.printStackTrace()
        }

    }

    private fun updatePreview() {
        if (null == cameraDevice) {
            log("updatePreview error, return")
        }
        captureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions?.setRepeatingRequest(captureRequestBuilder!!.build(), null, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread(CAMERA_THREAD_NAME)
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            handleErrorDefault(e)
        }

    }

    companion object {
        const val CAMERA_THREAD_NAME = "Camera Background"
    }
}
