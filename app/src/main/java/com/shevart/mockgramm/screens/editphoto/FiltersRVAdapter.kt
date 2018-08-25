package com.shevart.mockgramm.screens.editphoto

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseRVAdapter
import com.shevart.mockgramm.core.imageprocessing.ImageFilter
import com.shevart.mockgramm.util.inflate

@Suppress("unused")
// todo add selected/non_selected state views
class FiltersRVAdapter : BaseRVAdapter<ImageFilter, FiltersRVAdapter.ViewHolder>() {
    companion object {
        const val IMAGE_FILTER_SELECTED_VIEW_TYPE = 1
        const val IMAGE_FILTER_NON_SELECTED_VIEW_TYPE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflate(parent, R.layout.item_image_filter))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, viewType: Int) {
        // todo
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}