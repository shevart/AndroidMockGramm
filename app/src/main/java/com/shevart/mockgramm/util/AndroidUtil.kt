package com.shevart.mockgramm.util

import android.os.Build

fun isRuntimePermissionsRequired(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}