package com.shevart.mockgramm.base

import android.support.v7.widget.RecyclerView

@Suppress("unused")
abstract class BaseRVAdapter<M, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val items = ArrayList<M>()

    override fun getItemCount() = items.size

    protected fun updateItems(items: List<M>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    protected fun getItem(index: Int) = items.get(index)
}