package com.lhd.runapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.ActivityMain2Binding


class MainActivity2 : AppCompatActivity() {
    private lateinit var mBinding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        mBinding.mySeekBar.indicatorPositions = listOf(0F, 0.25F, 0.5F, 0.6F)
        mBinding.mySeekBar.indicatorText = listOf("0", "500", "1000", "4000")
    }
}