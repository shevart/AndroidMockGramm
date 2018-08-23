package com.shevart.mockgramm.camera

import android.graphics.SurfaceTexture
import android.view.TextureView

abstract class EmptyTextureSurfaceListener : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
    }
}