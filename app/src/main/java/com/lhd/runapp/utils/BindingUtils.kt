package com.lhd.runapp.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lhd.runapp.models.Challenger

object BindingUtils {
    @JvmStatic
    @BindingAdapter("android:loadImage")
    fun loadImage(img: ImageView, url: Int?) {
        if (url != null) {
            img.setImageResource(url)
        }
    }

    @JvmStatic
    @BindingAdapter("android:showProgress")
    fun showProgress(img: ImageView, item: Challenger) {
        val progressAfter = item.progress.toInt()
        val maxAfter = item.max.toInt()
        if (progressAfter / maxAfter < 1) {
            img.alpha = 0.3f
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("android:showText")
    fun showText(tv: TextView, item: Challenger) {
        val progressAfter = item.progress.toInt()
        val maxAfter = item.max.toInt()

        val progress = Utils.prettyCount(item.progress.toInt())
        val max = Utils.prettyCount(item.max.toInt())
        if (progressAfter / maxAfter < 1) {
            tv.text = "$progress/$max"
        } else {
            tv.text = item.date
        }
    }

}

