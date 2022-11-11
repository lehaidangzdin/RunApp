package com.lhd.runapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.lhd.runapp.databinding.ActivityMain3Binding
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils.getReadRequestData
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity3 : AppCompatActivity() {

    private lateinit var mBinding: ActivityMain3Binding

    private var ls: ArrayList<DataSet> = ArrayList()
    private var lsNameMonth: ArrayList<String> = ArrayList()
    private var lsData: ArrayList<BarEntry> = ArrayList()

    @SuppressLint("SimpleDateFormat")
    private val formatter = SimpleDateFormat("dd")

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd/MM/yyyy")
    private val date = Date()
    private val cal = Calendar.getInstance()
    private val currentYear = cal[Calendar.YEAR]
    private val currentMonth = cal[Calendar.MONTH] + 1

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .build()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main3)

        checkPermission()

    }

    private fun getAccount() =
        GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    //
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {
        for (i in 1..currentMonth) {
            var totalMonth = 0
            // lấy số ngày theo tháng
            val yearMonthObject = YearMonth.of(currentYear, i);
            val daysInMonth = yearMonthObject.lengthOfMonth()
            Log.e(TAG, "accessGoogleFit: start month: $i - current month: $currentMonth")
            Log.e(TAG, "accessGoogleFit: so ngay trong thang $i la: $daysInMonth")

            // set starttime = 01/01/{năm hiện tại}
            val startDate = "01/${i}/${currentYear}"
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal.time = sdf.parse(startDate)!!
            val startTime = cal.timeInMillis
            // end time
            if (i == currentMonth) {
                cal.time = Date()
            } else {
                val endDate = "${daysInMonth}/${i}/${currentYear}"
                cal.time = sdf.parse(endDate)!!
            }
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            val endTime = cal.timeInMillis

            // loge time
            val dateFormat: DateFormat = DateFormat.getDateInstance()
            Log.i(TAG, "end time: ${dateFormat.format(endTime)}")
            Log.i(TAG, "start time: ${dateFormat.format(startTime)}")

            // add month  vao ls
            Fitness.getHistoryClient(this, getAccount())
                .readData(getReadRequestData(startTime, endTime))
                .addOnSuccessListener { response ->
                    // 1 thang
                    for (bucket in response.buckets) {
                        var total = 0
                        //convert days in bucket to milliseconds
                        val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                        //convert milliseconds to date
                        val stepsDate = Date(days)
                        Log.e(TAG, "accessGoogleFit: $i - $stepsDate")
//                        step trong 1 ngay
                        for (dataSet in bucket.dataSets) {
                            total += dumpDataSet(dataSet)
                            Log.e(TAG, "accessGoogleFit: ${dumpDataSet(dataSet)}")
                        }
                        totalMonth += total
                        lsNameMonth.add(endTime.toString().substring(0, 4))
                        lsData.add(BarEntry(i.toFloat(), totalMonth.toFloat()))
                    }
                    lsNameMonth.add(i.toString().substring(0, 4))
                    lsData.add(BarEntry(i.toFloat(), totalMonth.toFloat()))
//                    Log.e(TAG, "accessGoogleFit: total $i ------$totalMonth")
                    displayChart(lsNameMonth, lsData)
                }
                .addOnFailureListener { e -> Log.d(TAG, "OnFailure()", e) }

        }
    }

    private fun displayChart(lsNameMonth: ArrayList<String>, lsData: ArrayList<BarEntry>) {

    }

    private fun dumpDataSet(dataSet: DataSet): Int {
        var totalSteps = 0;
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
                Toast.makeText(this, "permission dined", Toast.LENGTH_SHORT).show()
            }
        }
    }


}