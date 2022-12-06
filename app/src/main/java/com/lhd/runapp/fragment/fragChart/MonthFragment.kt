package com.lhd.runapp.fragment.fragChart

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarEntry
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.FragmentMonthBinding
import com.lhd.runapp.utils.Utils
import com.lhd.runapp.viewmodel.HomeViewModel
import kotlin.collections.ArrayList

class MonthFragment : Fragment() {

    private lateinit var mBinding: FragmentMonthBinding
    private lateinit var viewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMonthBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
//        viewModel.getStepsByMonth()
        observerComponent()
        return mBinding.root
    }

    private fun observerComponent() {
        viewModel.dataChartByMonth.observe(viewLifecycleOwner) {
            displayChart(it.lsAxis, it.lsBarEntry)
        }
    }

    private fun displayChart(
        lsAxis: ArrayList<String>,
        lsBarEntries: ArrayList<BarEntry>
    ) {
        SetupChart(context, mBinding.barChart, lsBarEntries, lsAxis, Utils.MAX_MONTH.toFloat())
            .applyOptions()
    }


}