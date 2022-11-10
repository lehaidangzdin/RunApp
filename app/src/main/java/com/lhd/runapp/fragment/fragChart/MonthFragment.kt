package com.lhd.runapp.fragment.fragChart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarEntry
import com.lhd.runapp.R
import com.lhd.runapp.customviews.MyMarkerView
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.FragmentMonthBinding
import dagger.hilt.android.AndroidEntryPoint

class MonthFragment : Fragment() {

    private lateinit var mBinding: FragmentMonthBinding
    private var barEntriesList: ArrayList<BarEntry> = ArrayList()
    private val lsAxis: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMonthBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val setUpChart =
            activity?.let {
                SetupChart(
                    it.applicationContext,
                    mBinding.barChart,
                    barEntriesList,
                    lsAxis,
                    120000f

                )
            }
        setUpChart?.applyOptions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBarChartData()
        setAxis()
    }

    private fun getBarChartData() {
        barEntriesList.add(BarEntry(1f, 100f))
        barEntriesList.add(BarEntry(2f, 350f))
        barEntriesList.add(BarEntry(3f, 210f))
        barEntriesList.add(BarEntry(4f, 110f))
        barEntriesList.add(BarEntry(5f, 320f))
        barEntriesList.add(BarEntry(6f, 330f))
        barEntriesList.add(BarEntry(7f, 330f))
        barEntriesList.add(BarEntry(8f, 330f))
        barEntriesList.add(BarEntry(9f, 330f))
        barEntriesList.add(BarEntry(10f, 330f))
        barEntriesList.add(BarEntry(11f, 233f))
        barEntriesList.add(BarEntry(12f, 110f))
    }

    private fun setAxis() {
        lsAxis.add("")
        lsAxis.add("T1")
        lsAxis.add("T2")
        lsAxis.add("T3")
        lsAxis.add("T4")
        lsAxis.add("T5")
        lsAxis.add("T6")
        lsAxis.add("T7")
        lsAxis.add("T8")
        lsAxis.add("T9")
        lsAxis.add("T10")
        lsAxis.add("T11")
        lsAxis.add("T12")
    }


}