package com.shevart.mockgramm.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

object CameraPermission {
    fun isGranted(context: Context): Boolean {
        return if (isRuntimePermissionsRequired()) {
            isPermissionGranted(context)
        } else {
            true
        }
    }

    private fun isPermissionGranted(context: Context) =
            context.isPermissionGranted(Manifest.permission.CAMERA)
}

object WriteStoragePermission {
    fun isGranted(context: Context): Boolean {
        return if (isRuntimePermissionsRequired()) {
            isPermissionGranted(context)
        } else {
            true
        }
    }

    private fun isPermissionGranted(context: Context) =
            context.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

fun Context.isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED