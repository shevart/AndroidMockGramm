package com.shevart.mockgramm.core

import android.util.SparseIntArray
import android.view.Surface

val ORIENTATIONS: SparseIntArray by lazy {
    SparseIntArray().apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }
}

object CameraConfig {
    const val DEFAULT_WIDTH = 640
    const val DEFAULT_HEIGHT = 480
}

