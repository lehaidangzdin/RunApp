package com.lhd.runapp.fragment.fragChart

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.FragmentDayBinding
import com.lhd.runapp.presenter.HomePresenter
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils.fitnessOptions
import com.lhd.runapp.utils.Utils.getAccount
import com.lhd.runapp.utils.Utils.getTimeNow
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
        viewModel.checkPermission(requireActivity())
        observerChart()
        return mBinding.root
    }

    private fun observerChart() {
        viewModel.dataChart.observe(viewLifecycleOwner) {
            displayChart(it.lsAxis, it.lsBarEntry)
        }
    }

    private fun displayChart(lsAxis: ArrayList<String>, lsBarEntries: ArrayList<BarEntry>) {
        SetupChart(context, mBinding.barChart, lsBarEntries, lsAxis, 4000f)
            .applyOptions()
    }

}