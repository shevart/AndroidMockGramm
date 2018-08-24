@file:Suppress("unused")

package com.shevart.mockgramm.core.util

import android.app.Activity
import android.support.v4.app.Fragment

fun Activity.provideScreenRotation() =
        this.windowManager.defaultDisplay.rotation

fun Fragment.provideScreenRotation() =
        this.activity!!.windowManager.defaultDisplay.rotation