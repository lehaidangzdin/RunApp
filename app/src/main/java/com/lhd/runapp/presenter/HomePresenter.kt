package com.lhd.runapp.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.lhd.runapp.models.DataChart
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils
import com.lhd.runapp.utils.Utils.dumpDataSet
import com.lhd.runapp.utils.Utils.fitnessOptions
import com.lhd.runapp.utils.Utils.getAccount
import com.lhd.runapp.utils.Utils.getTimeNow
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

const val TAG = "HomePresenter"

class HomePresenter(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private var xFloat = 0
    val totalSteps = ObservableField<Int>()
    val process = MutableLiveData<Float>()
    val dataChart = MutableLiveData<DataChart>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission(activity: Activity) {
        totalSteps.set(0)
        process.value = 0f
        if (!GoogleSignIn.hasPermissions(getAccount(context), fitnessOptions)) {
            Log.e(
                TAG,
                "checkPermission: ${
                    GoogleSignIn.hasPermissions(
                        getAccount(context),
                        fitnessOptions
                    )
                }"
            )
            GoogleSignIn.requestPermissions(
                activity,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getAccount(context),
                fitnessOptions
            )
        } else {
            recordingClient()
            getStepsByWeek()
        }
    }

    private fun recordingClient() {
        Fitness.getRecordingClient(
            context.applicationContext,
            GoogleSignIn.getAccountForExtension(context.applicationContext, fitnessOptions)
        )
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnSuccessListener {
                Log.i(TAG, "Subscription was successful!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem subscribing ", e)
            }
    }

    private fun getStepsByCurrentDay(lsXAxis: ArrayList<String>, lsBarEntry: ArrayList<BarEntry>) {
        Fitness.getHistoryClient(context.applicationContext, getAccount(context))
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = when {
                    dataSet.isEmpty -> 0
                    else -> dataSet.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                xFloat++
                totalSteps.set(total)
                process.value = total.toFloat()
                lsXAxis.add(getTimeNow().toString().substring(0, 4))
                lsBarEntry.add(BarEntry(xFloat.toFloat(), total.toFloat()))
                Log.e(TAG, "getStepsByCurrentDay: ${totalSteps.get()}")
                val barEntry = DataChart(lsXAxis, lsBarEntry)
                dataChart.value = barEntry
//                Log.i(TAG, "Total steps: $total")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem getting the step count.", e)
            }
            
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStepsByWeek() {
        val lsXAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()

        lsXAxis.add("")
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
        Log.i(TAG, "aaaaaaaa -${dateFormat.format(endTime)}")
        Log.i(TAG, "aaaaaaaaa -${dateFormat.format(startTime)}")
        Log.i(TAG, "getStepsByWeek: ${Date(endTime)}")

        Fitness.getHistoryClient(context, getAccount(context))
            .readData(Utils.getReadRequestData(startTime, endTime))
            .addOnSuccessListener { response ->
                for (bucket in response.buckets) {
                    //convert days in bucket to milliseconds
                    val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    //convert milliseconds to date
                    val stepsDate = Date(days)
                    // add day vao ls
                    lsXAxis.add(stepsDate.toString().substring(0, 4))
//                    Log.e(com.lhd.runapp.fragment.fragChart.TAG, "accessGoogleFit: $stepsDate")
                    xFloat++
                    for (dataSet in bucket.dataSets) {
                        lsBarEntry.add(
                            BarEntry(
                                xFloat.toFloat(),
                                dumpDataSet(dataSet)
                            )
                        )
//                        Log.e(
//                            com.lhd.runapp.fragment.fragChart.TAG,
//                            "accessGoogleFit: ${dumpDataSet(dataSet)}"
//                        )
                    }
                }
                getStepsByCurrentDay(lsXAxis, lsBarEntry)

            }
            .addOnFailureListener { e ->
                Log.d(com.lhd.runapp.fragment.fragChart.TAG, "OnFailure()", e)
            }
    }


    override fun onCleared() {
        super.onCleared()
        // Dispose All your Subscriptions to avoid memory leaks
    }

}