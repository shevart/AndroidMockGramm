package com.shevart.mockgramm.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

object CameraPermission {
    fun isGranted(context: Context): Boolean {
        return if (isRuntimePermissionsRequired()) {
            isCameraPermissionGranted(context)
        } else {
            true
        }
    }

    private fun isCameraPermissionGranted(context: Context): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }
}