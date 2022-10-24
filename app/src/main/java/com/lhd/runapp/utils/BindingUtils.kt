package com.lhd.runapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object BindingUtils {
    @JvmStatic
    @BindingAdapter("android:loadImage")
    fun loadImage(img: ImageView, url: Int?) {
        if (url != null) {
            img.setImageResource(url)
        }
    }

}

