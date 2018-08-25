package com.shevart.mockgramm.util

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

const val SEPARATOR = "/"

fun createPhotoFile(fileName: String): File {
    return File(Environment.getExternalStorageDirectory().toString() + SEPARATOR + fileName)
}

@Throws(IOException::class)
fun File.save(bytes: ByteArray) {
    var output: OutputStream? = null
    try {
        output = FileOutputStream(this)
        output.write(bytes)
    } finally {
        output?.close()
    }
}