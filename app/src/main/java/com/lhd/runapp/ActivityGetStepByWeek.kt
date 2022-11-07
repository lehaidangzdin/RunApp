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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.request.DataReadRequest
import com.lhd.runapp.FitRequestCode.GG_FIT_REQUEST_CODE
import com.lhd.runapp.databinding.ActivityMain3Binding
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


enum class FitRequestCode {
    GG_FIT_REQUEST_CODE
}

class MainActivity3 : AppCompatActivity() {

    private lateinit var mBinding: ActivityMain3Binding

    private var ls: ArrayList<DataSet> = ArrayList()
    private var lsDay: ArrayList<String> = ArrayList()
    private var stepByMonth = 0

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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {


        // lấy step tuần trc
//        val cal = Calendar.getInstance()
//        cal.time = Date()
//        cal[Calendar.HOUR_OF_DAY] = 0
//        cal[Calendar.MINUTE] = 0
//        cal[Calendar.SECOND] = 0
//        val endTime = cal.timeInMillis
//
//        cal.add(Calendar.WEEK_OF_MONTH, -1)
//        cal[Calendar.HOUR_OF_DAY] = 0
//        cal[Calendar.MINUTE] = 0
//        cal[Calendar.SECOND] = 0
//        val startTime = cal.timeInMillis
//        Log.i(TAG, endTime.toString())
//        Log.i(TAG, startTime.toString())


//       lấy step từ 6 ngày trc đên hôm nay
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        val dateFormat: DateFormat = getDateInstance()
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime))
        Log.i(TAG, "Range End: " + dateFormat.format(endTime))

        //       lấy step theo ngày này tháng trc đên hôm nay
//        val cal = Calendar.getInstance()
//        val now = Date()
//        cal.time = now
//        val endTime = cal.timeInMillis
//        cal.add(Calendar.MONTH, -1)
//        val startTime = cal.timeInMillis
//
//        val dateFormat: DateFormat = getDateInstance()
//        Log.i(TAG, "Range Start: " + dateFormat.format(startTime))
//        Log.i(TAG, "Range End: " + dateFormat.format(endTime))

        val ESTIMATED_STEP_DELTAS: DataSource = DataSource.Builder()
            .setDataType(TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .setAppPackageName("com.google.android.gms")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()


        Fitness.getHistoryClient(this, getAccount())
            .readData(readRequest)
            .addOnSuccessListener { response ->

                for (bucket in response.buckets) {
                    //convert days in bucket to milliseconds
                    val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    //convert milliseconds to date
                    val stepsDate = Date(days)
                    //convert date to day of the week eg: monday, tuesday etc
//                    @SuppressLint("SimpleDateFormat")
//                    val df: DateFormat = SimpleDateFormat("EEE")
//                    val weekday = df.format(stepsDate)
//                    Log.i(TAG, stepsDate.toString())
                    // add day vao ls
                    lsDay.add(stepsDate.toString().substring(0, 4))
                    Log.e(TAG, "accessGoogleFit: ${stepsDate.toString().substring(0, 4)}")

                    for (dataSet in bucket.dataSets) {
                        stepByMonth += dumpDataSet(dataSet)
                        Log.e(TAG, "accessGoogleFit: ${dumpDataSet(dataSet)}")
                    }
                }
                Log.i(TAG, "$stepByMonth")
                Log.i(TAG, ls.toString())
            }
            .addOnFailureListener { e -> Log.d(TAG, "OnFailure()", e) }
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
                GG_FIT_REQUEST_CODE.ordinal,
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
                GG_FIT_REQUEST_CODE.ordinal -> accessGoogleFit()
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