package com.shevart.mockgramm.screens.editphoto

import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseFragment

class EditPhotoFragment : BaseFragment() {
    override fun provideLayoutResId() = R.layout.fragment_edit_photo

    companion object {
        // todo pass here image URI
        fun getInstance(): EditPhotoFragment {
            return getInstance()
        }
    }
}