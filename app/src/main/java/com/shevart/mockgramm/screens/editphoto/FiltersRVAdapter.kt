package com.shevart.mockgramm.screens.editphoto

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.shevart.mockgramm.R
import com.shevart.mockgramm.base.BaseRVAdapter
import com.shevart.mockgramm.core.imageprocessing.ImageFilter
import com.shevart.mockgramm.util.inflate

class FiltersRVAdapter : BaseRVAdapter<ImageFilter, RecyclerView.ViewHolder>() {
    var listener: FiltersListListener? = null
    private var selectedFilterIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            IMAGE_FILTER_NON_SELECTED_VIEW_TYPE -> createNonSelectedFilterViewHolder(parent)
            IMAGE_FILTER_SELECTED_VIEW_TYPE -> createSelectedFilterViewHolder(parent)
            else -> throw IllegalArgumentException("Check it!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == selectedFilterIndex) {
            IMAGE_FILTER_SELECTED_VIEW_TYPE
        } else {
            IMAGE_FILTER_NON_SELECTED_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (viewHolder) {
            is NonSelectedFilterViewHolder -> bind(viewHolder, item)
            is SelectedFilterViewHolder -> bind(viewHolder, item)
            else -> throw IllegalArgumentException("Check it!")
        }
    }

    private fun bind(holder: NonSelectedFilterViewHolder, item: ImageFilter) {
        holder.ivFilterCover.setImageResource(item.filterCoverResId)
    }

    private fun bind(holder: SelectedFilterViewHolder, item: ImageFilter) {
        holder.ivFilterCover.setImageResource(item.filterCoverResId)
    }

    private fun createNonSelectedFilterViewHolder(parent: ViewGroup): NonSelectedFilterViewHolder {
        return NonSelectedFilterViewHolder(inflate(parent, R.layout.item_image_filter)).apply {
            setFilterItemClickListener(ivFilterCover, this)
        }
    }

    private fun createSelectedFilterViewHolder(parent: ViewGroup): NonSelectedFilterViewHolder {
        return NonSelectedFilterViewHolder(inflate(parent, R.layout.item_image_filter_selected)).apply {
            setFilterItemClickListener(ivFilterCover, this)
        }
    }

    private fun setFilterItemClickListener(ivFilterCover: ImageView, holder: RecyclerView.ViewHolder) {
        ivFilterCover.setOnClickListener {
            val index = holder.adapterPosition
            if (index != -1) {
                selectedFilterIndex = index
                listener?.onFilterSelected(getItem(index))
                notifyDataSetChanged()
            }
        }
    }

    class NonSelectedFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFilterCover: ImageView = itemView.findViewById(R.id.ivFilterCover)
    }

    class SelectedFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFilterCover: ImageView = itemView.findViewById(R.id.ivFilterCover)
    }

    interface FiltersListListener {
        fun onFilterSelected(filter: ImageFilter)
    }

    companion object {
        const val IMAGE_FILTER_SELECTED_VIEW_TYPE = 1
        const val IMAGE_FILTER_NON_SELECTED_VIEW_TYPE = 2
    }
}