package com.shevart.mockgramm.camera.util

import android.app.Activity
import android.support.v4.app.Fragment

fun Activity.provideScreenRotation() =
        this.windowManager.defaultDisplay.rotation

fun Fragment.provideScreenRotation() =
        this.activity!!.windowManager.defaultDisplay.rotation