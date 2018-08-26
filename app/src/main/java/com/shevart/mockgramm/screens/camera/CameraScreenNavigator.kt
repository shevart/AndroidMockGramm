package com.shevart.mockgramm.screens.camera

import android.net.Uri

interface CameraScreenNavigator {
    fun openPhoto(photoUri: Uri)
}