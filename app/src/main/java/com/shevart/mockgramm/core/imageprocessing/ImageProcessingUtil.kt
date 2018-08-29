package com.shevart.mockgramm.core.imageprocessing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.widget.ImageView

fun changeBitmapColor(sourceBitmap: Bitmap, image: ImageView, color: Int) {

    val resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
            sourceBitmap.width - 1, sourceBitmap.height - 1)
    val p = Paint()
    val filter = LightingColorFilter(color, 1)
    p.colorFilter = filter
    p.alpha = 150
    image.setImageBitmap(resultBitmap)

    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(resultBitmap, 0f, 0f, p)
}