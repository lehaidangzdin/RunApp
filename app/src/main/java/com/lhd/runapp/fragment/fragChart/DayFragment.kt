package com.lhd.runapp.fragment.fragChart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.lhd.runapp.FitRequestCode
import com.lhd.runapp.customviews.SetupChart

import com.lhd.runapp.databinding.FragmentDayBinding
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


const val TAG = "DayFragment"

class DayFragment() : Fragment() {

    private lateinit var mBinding: FragmentDayBinding
    private var xFloat = 0f
    private val barEntriesList: ArrayList<BarEntry> = ArrayList()
    private val lsAxis: ArrayList<String> = ArrayList()

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .build()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDayBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        checkPermission()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lsAxis.add("")
    }

    private fun getAccount() =
        GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)

    //
    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {

//       lấy step từ 6 ngày trc đên hôm nay
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        val dateFormat: DateFormat = DateFormat.getDateInstance()
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime))
        Log.i(TAG, "Range End: " + dateFormat.format(endTime))

        val ESTIMATED_STEP_DELTAS: DataSource = DataSource.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .setAppPackageName("com.google.android.gms")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()


        Fitness.getHistoryClient(requireActivity(), getAccount())
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (bucket in response.buckets) {
                    //convert days in bucket to milliseconds
                    val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    //convert milliseconds to date
                    val stepsDate = Date(days)
                    // add day vao ls
                    lsAxis.add(stepsDate.toString().substring(0, 4))
                    Log.e(TAG, "accessGoogleFit: ${stepsDate.toString().substring(0, 4)}")
                    xFloat++
                    for (dataSet in bucket.dataSets) {
                        barEntriesList.add(BarEntry(xFloat, dumpDataSet(dataSet)))
                        Log.e(TAG, "accessGoogleFit: ${dumpDataSet(dataSet)}")
                    }
                }
                displayBarChart()

            }
            .addOnFailureListener { e -> Log.d(TAG, "OnFailure()", e) }
    }

    private fun displayBarChart() {
        Log.e(TAG, "displayBarChart: ${lsAxis.size}")
        Log.e(TAG, "displayBarChart: ${barEntriesList.size}")

        val setUpChart =
            activity?.let {
                SetupChart(
                    it.applicationContext,
                    mBinding.barChart,
                    barEntriesList,
                    lsAxis
                )
            }
        setUpChart?.setUp()
    }

    private fun dumpDataSet(dataSet: DataSet): Float {
        var totalSteps = 0f;
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                totalSteps += dp.getValue(field).asInt()
            }
        }
        return totalSteps
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermission() {

        if (!GoogleSignIn.hasPermissions(getAccount(), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getAccount(),
                fitnessOptions
            )
        } else {
            accessGoogleFit()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal -> accessGoogleFit()
                else -> {
                    // Result wasn't from Google Fit
                }
            }
            else -> {
                // Permission not granted
                Toast.makeText(requireActivity(), "permission dined", Toast.LENGTH_SHORT).show()
            }
        }
    }

}