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
import com.lhd.runapp.databinding.FragmentDayBinding
import com.lhd.runapp.viewmodel.HomePresenter
import java.util.*

const val TAG = "DayFragment"

class DayFragment : Fragment() {

    private lateinit var mBinding: FragmentDayBinding
    private var xFloat = 0f
    private lateinit var viewModel: HomePresenter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDayBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomePresenter::class.java]
        viewModel.getStepsByDayOfWeek()
        observerChart()
        return mBinding.root
    }

    private fun observerChart() {
        viewModel.dataChartByWeek.observe(viewLifecycleOwner) {
//            if (it.lsBarEntry.size == 7) {
                displayChart(it.lsAxis, it.lsBarEntry)
//            }
        }
    }

    private fun displayChart(lsAxis: ArrayList<String>, lsBarEntries: ArrayList<BarEntry>) {
        SetupChart(context, mBinding.barChart, lsBarEntries, lsAxis, 4000f)
            .applyOptions()
    }

}