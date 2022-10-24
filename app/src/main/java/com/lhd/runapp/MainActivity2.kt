package com.lhd.runapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.model.GradientColor
import com.lhd.runapp.customviews.MyMarkerView
import com.lhd.runapp.databinding.ActivityMain2Binding


class MainActivity2 : AppCompatActivity() {
    private lateinit var mBinding: ActivityMain2Binding

    lateinit var barData: BarData
    lateinit var barDataSet: BarDataSet

    private lateinit var barEntriesList: ArrayList<BarEntry>
    private val axiss: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        getBarChartData()
        barDataSet = BarDataSet(barEntriesList, "")
        barDataSet.setDrawValues(false)
        // on below line we are initializing our bar data
        barData = BarData(barDataSet)
        // on below line we are setting data to our bar chart
        mBinding.barChart.data = barData
        val l = mBinding.barChart.legend
        l.isEnabled = false
        // on below line we are setting text size
        barDataSet.valueTextSize = 20f
        // set gradient color column

        val startColor = ContextCompat.getColor(this, R.color.gradient2)
        val endColor = ContextCompat.getColor(this, R.color.gradient3)
        val gradientColors: MutableList<GradientColor> = ArrayList()
        gradientColors.add(GradientColor(endColor, startColor))
        barDataSet.gradientColors = gradientColors
        // change position xAxis
        val xAxis: XAxis = mBinding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        setAxis()
        xAxis.valueFormatter = IndexAxisValueFormatter(axiss);

        // hide axis right
        mBinding.barChart.axisRight.isEnabled = false

        // hide line in background
        mBinding.barChart.xAxis.setDrawGridLines(false)
        mBinding.barChart.axisLeft.setDrawGridLines(false)
        mBinding.barChart.axisRight.setDrawGridLines(false)
        // set text color
        mBinding.barChart.xAxis.textColor = Color.WHITE
        mBinding.barChart.axisLeft.textColor = Color.WHITE
        // change background
        mBinding.barChart.setBackgroundResource(R.drawable.bg_chart)
        mBinding.barChart.description.isEnabled = false

        // touch column
        mBinding.barChart.setTouchEnabled(true)
        val mv = MyMarkerView(this, R.layout.my_marker)
        mBinding.barChart.marker = mv
    }

    private fun getBarChartData() {
        barEntriesList = ArrayList()
        // on below line we are adding data
        // to our bar entries list
        barEntriesList.add(BarEntry(1f, 1000f))
        barEntriesList.add(BarEntry(2f, 3565f))
        barEntriesList.add(BarEntry(3f, 2211f))
        barEntriesList.add(BarEntry(4f, 3111f))
        barEntriesList.add(BarEntry(5f, 1232f))
        barEntriesList.add(BarEntry(6f, 633f))
        barEntriesList.add(BarEntry(7f, 700f))
    }

    private fun setAxis() {
        axiss.add("")
        axiss.add("Mon")
        axiss.add("Tue")
        axiss.add("Wed")
        axiss.add("Thu")
        axiss.add("Fir")
        axiss.add("Sat")
        axiss.add("Sun")
    }
}