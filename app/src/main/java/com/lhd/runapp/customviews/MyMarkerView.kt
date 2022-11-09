package com.lhd.runapp.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.lhd.runapp.R
import com.lhd.runapp.utils.Utils


@SuppressLint("ViewConstructor")
class MyMarkerView(
    context: Context,
    layout: Int,
) : MarkerView(context, layout) {
    private var tvContent: TextView? = null

    init {
        tvContent = findViewById(R.id.tvContent)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            tvContent?.text = "${e?.y?.toInt()?.let { Utils.prettyCount(it) }}"
        } catch (e: IndexOutOfBoundsException) {
            Log.e("MyMarkerView", "refreshContent: ${e.message}")
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}