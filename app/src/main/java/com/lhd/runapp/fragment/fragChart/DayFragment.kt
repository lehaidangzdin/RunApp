package com.lhd.runapp.fragment.fragChart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarEntry
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.FragmentDayBinding


class DayFragment() : Fragment() {

    private var mBinding: FragmentDayBinding? = null
    private val binding get() = mBinding!!
    private lateinit var barEntriesList: ArrayList<BarEntry>
    private val lsAxis: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater, com.lhd.runapp.R.layout.fragment_day, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val setUpChart =
            activity?.let {
                SetupChart(
                    it.applicationContext,
                    mBinding!!.barChart,
                    barEntriesList,
                    lsAxis
                )
            }
        setUpChart?.setUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBarChartData()
        setAxis()
    }

    private fun getBarChartData() {
        barEntriesList = ArrayList()
        barEntriesList.add(BarEntry(1f, 100f))
        barEntriesList.add(BarEntry(2f, 350f))
        barEntriesList.add(BarEntry(3f, 210f))
        barEntriesList.add(BarEntry(4f, 110f))
        barEntriesList.add(BarEntry(5f, 320f))
        barEntriesList.add(BarEntry(6f, 330f))
        barEntriesList.add(BarEntry(7f, 330f))
    }

    private fun setAxis() {
        lsAxis.add("")
        lsAxis.add("Mon")
        lsAxis.add("Tue")
        lsAxis.add("Wed")
        lsAxis.add("Thu")
        lsAxis.add("Fir")
        lsAxis.add("Sat")
        lsAxis.add("Sun")
    }
}