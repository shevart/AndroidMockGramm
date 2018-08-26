package com.shevart.mockgramm.screens

import android.net.Uri
import android.os.Bundle
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseActivity
import com.shevart.mockgramm.screens.camera.CameraFragment
import com.shevart.mockgramm.screens.camera.CameraScreenNavigator
import com.shevart.mockgramm.screens.editphoto.EditPhotoFragment

class MainActivity : BaseActivity(), CameraScreenNavigator {
    override fun provideLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportFragmentManager.findFragmentById(R.id.flContainer) == null) {
            showCameraScreen()
        }
    }

    override fun openPhoto(photoUri: Uri) {
        showEditPhotoScreen(photoUri)
    }

    private fun showCameraScreen() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.flContainer, CameraFragment.getInstance())
                .commit()
    }

    private fun showEditPhotoScreen(photoUri: Uri) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.flContainer, EditPhotoFragment.getInstance(photoUri))
                .addToBackStack(EditPhotoFragment.TAG)
                .commit()
    }
}
