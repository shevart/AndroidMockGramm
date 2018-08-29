package com.shevart.mockgramm.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.FileNotFoundException
import java.io.IOException

@Throws(FileNotFoundException::class, IOException::class)
fun Uri.openBitmap(context: Context): Bitmap? {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, this)
}

fun Bitmap.copyBitmap() =
        Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)