package com.lhd.runapp.utils

import android.content.res.Resources
import android.graphics.Bitmap

object Utils {
    fun convertDpToPixels(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * resize image in drawable
     */
    fun resizeBitmap(bitmap: Bitmap, toWidth: Int, toHeight: Int): Bitmap {

        return Bitmap.createScaledBitmap(
            bitmap,
            convertDpToPixels(toWidth),
            convertDpToPixels(toHeight),
            false
        )
    }
}