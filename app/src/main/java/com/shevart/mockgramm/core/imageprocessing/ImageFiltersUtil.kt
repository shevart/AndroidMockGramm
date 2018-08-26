package com.shevart.mockgramm.core.imageprocessing

import com.shevart.mockgramm.R

fun createImageFiltersList(): List<ImageFilter> {
    return listOf(
            ImageFilter(R.drawable.none_filter_cover),
            ImageFilter(R.drawable.first_filter_cover),
            ImageFilter(R.drawable.second_filter_cover),
            ImageFilter(R.drawable.third_filter_cover))
}