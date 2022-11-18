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
import com.lhd.runapp.databinding.FragmentWeekBinding
import com.lhd.runapp.viewmodel.HomePresenter

class WeekFragment : Fragment() {

    private lateinit var mBinding: FragmentWeekBinding
    private lateinit var viewModel: HomePresenter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentWeekBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomePresenter::class.java]
        viewModel.getStepsByWeekOfMonth()
        observerComponent()

        return mBinding.root
    }

    private fun observerComponent() {
        viewModel.dataChartByWeekOfMonth.observe(viewLifecycleOwner) {
            displayChart(it.lsAxis, it.lsBarEntry)
        }
    }

    private fun displayChart(
        lsAxis: ArrayList<String>,
        lsBarEntries: ArrayList<BarEntry>
    ) {
        SetupChart(context, mBinding.barChart, lsBarEntries, lsAxis, 28000f)
            .applyOptions()
    }
}