package com.shevart.mockgramm.screens.editphoto

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseFragment
import com.shevart.mockgramm.util.getPhotoUri
import com.shevart.mockgramm.util.setPhotoUri
import kotlinx.android.synthetic.main.fragment_edit_photo.*

@Suppress("unused")
class EditPhotoFragment : BaseFragment() {
    private val photoUri: Uri
        get() = arguments?.getPhotoUri()
                ?: throw IllegalArgumentException("You must pass photoUri as argument!")

    override fun provideLayoutResId() = R.layout.fragment_edit_photo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivBack.setOnClickListener { backByBackButton() }

        val bitmap = MediaStore.Images.Media.getBitmap(forceContext.contentResolver, photoUri)
        if (bitmap != null) {
            ivEditedPhoto.setImageBitmap(bitmap)
        } else {
            showToast("bitmap is null!")
        }
    }

    companion object {
        const val TAG = "EditPhoto"

        fun getInstance(photoUri: Uri): EditPhotoFragment {
            return EditPhotoFragment().apply {
                arguments = Bundle(1)
                        .setPhotoUri(photoUri)
            }
        }
    }
}