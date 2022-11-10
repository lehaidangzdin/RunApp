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

    //================================================================================================
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStepsByWeek() {
        //
        val lsBarEntries = ArrayList<BarEntry>()
        val lsAxis = ArrayList<String>()
        lsAxis.add("")

//        //lấy step 1 tuần trc
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val endTime = cal.timeInMillis

        cal.add(Calendar.DAY_OF_WEEK, -6)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTime = cal.timeInMillis

        val dateFormat: DateFormat = DateFormat.getDateInstance()
        Log.i(TAG, "aaaaaaaaaaaaa -${dateFormat.format(endTime)}")
        Log.i(TAG, "aaaaaaaaaaaaa -${dateFormat.format(startTime)}")
        Log.i(TAG, "getStepsByWeek: ${Date(endTime)}")

        val estimatedStepDeltas: DataSource = DataSource.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .setAppPackageName("com.google.android.gms")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(estimatedStepDeltas, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        Fitness.getHistoryClient(requireActivity(), getAccount(requireContext()))
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (bucket in response.buckets) {
                    //convert days in bucket to milliseconds
                    val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    //convert milliseconds to date
                    val stepsDate = Date(days)
                    // add day vao ls
                    lsAxis.add(stepsDate.toString().substring(0, 4))
                    Log.e(TAG, "accessGoogleFit: $stepsDate")
                    xFloat++
                    for (dataSet in bucket.dataSets) {
                        lsBarEntries.add(BarEntry(xFloat, dumpDataSet(dataSet)))
                        Log.e(TAG, "accessGoogleFit: ${dumpDataSet(dataSet)}")
                    }
                }
                getStepsByCurrentDay(lsAxis, lsBarEntries)

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "OnFailure()", e)
            }
    }

    private fun getStepsByCurrentDay(lsAxis: ArrayList<String>, lsBarEntries: ArrayList<BarEntry>) {


        Log.i(TAG, "getStepsByWeek: ${getTimeNow()}")

        Fitness.getHistoryClient(requireActivity(), getAccount(requireContext()))
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
//                val days = getStartTime(TimeUnit.MILLISECONDS)
                val total = when {
                    dataSet.isEmpty -> 0
                    else -> dataSet.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                xFloat++
                lsAxis.add(getTimeNow().toString().substring(0, 4))
                lsBarEntries.add(BarEntry(xFloat, total.toFloat()))
                displayChart(lsAxis, lsBarEntries)
            }
            .addOnFailureListener { e ->
                Log.e(com.lhd.runapp.fragment.TAG, "There was a problem getting the step count.", e)
            }
    }

    private fun displayChart(lsAxis: ArrayList<String>, lsBarEntries: ArrayList<BarEntry>) {
        SetupChart(context, mBinding.barChart, lsBarEntries, lsAxis, 4000f)
            .applyOptions()
    }

    private fun dumpDataSet(dataSet: DataSet): Float {
        var totalSteps = 0f
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                totalSteps += dp.getValue(field).asInt()
            }
        }
        return totalSteps
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermission() {
        if (!GoogleSignIn.hasPermissions(getAccount(requireContext()), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getAccount(requireContext()),
                fitnessOptions
            )
        } else {
            getStepsByWeek()
        }
    }
}