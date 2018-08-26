package com.shevart.mockgramm.util

import android.view.LayoutInflater
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup

fun inflate(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
}