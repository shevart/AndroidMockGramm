@file:Suppress("unused")

package com.shevart.mockgramm.util

import android.net.Uri
import android.os.Bundle

private const val PHOTO_URI_KEY = "PHOTO_URI_KEY"

fun Bundle.setPhotoUri(photoUri: Uri) = this.apply {
    putParcelable(PHOTO_URI_KEY, photoUri)
}

fun Bundle.getPhotoUri(): Uri? =
        this.getParcelable(PHOTO_URI_KEY)