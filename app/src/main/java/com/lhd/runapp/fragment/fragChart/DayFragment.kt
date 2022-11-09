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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.lhd.runapp.customviews.SetupChart
import com.lhd.runapp.databinding.FragmentDayBinding
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils.fitnessOptions
import com.lhd.runapp.utils.Utils.getAccount
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val TAG = "DayFragment"

class DayFragment : Fragment() {

    private lateinit var mBinding: FragmentDayBinding
    private var xFloat = 0f


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    //
    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {
        //
        val lsBarEntries = ArrayList<BarEntry>()
        val lsAxis = ArrayList<String>()
        lsAxis.add("")

//        //lấy step 1 tuần trc
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis
        val dateFormat: DateFormat = DateFormat.getDateInstance()
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime))
        Log.i(TAG, "Range End: " + dateFormat.format(endTime))

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
                displayChart(lsAxis, lsBarEntries)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "OnFailure()", e)
            }
    }

    private fun displayChart(lsAxis: ArrayList<String>, lsBarEntries: ArrayList<BarEntry>) {
        SetupChart(context, mBinding.barChart, lsBarEntries, lsAxis, 4000f)
            .applyOptions()
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
        if (!GoogleSignIn.hasPermissions(getAccount(requireContext()), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getAccount(requireContext()),
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
//                Toast.makeText(requireActivity(), "permission dined", Toast.LENGTH_SHORT).show()
            }
        }
    }

}