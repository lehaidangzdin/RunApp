package com.lhd.runapp.fragment.fragChart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarEntry
import com.lhd.runapp.R
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.FragmentWeekBinding

class WeekFragment : Fragment() {

    private var mBinding: FragmentWeekBinding? = null
    private val binding get() = mBinding!!
    private lateinit var barEntriesList: ArrayList<BarEntry>
    private val lsAxis: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_week, container, false
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
        barEntriesList.add(BarEntry(5f, 510f))
        barEntriesList.add(BarEntry(6f, 0f))
    }

    private fun setAxis() {
        lsAxis.add("")
        lsAxis.add("Tuan 1")
        lsAxis.add("Tuan 2")
        lsAxis.add("Tuan 3")
        lsAxis.add("Tuan 4")
        lsAxis.add("Tuan 5")
        lsAxis.add("Tuan 6")

    }

}