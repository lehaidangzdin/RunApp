package com.lhd.runapp.presenter

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.fitness.Fitness
import com.lhd.runapp.models.DataChart
import com.lhd.runapp.utils.Utils
import com.lhd.runapp.utils.Utils.dumpDataSet
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


const val TAG_WEEKPRESENTER = "WeekPresenter"

class WeekPresenter(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private var xFloat = 0
    val totalSteps = ObservableField<Int>()
    val process = MutableLiveData<Float>()
    val dataChart = MutableLiveData<DataChart>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByWeek() {
        val lsXAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()
        val cal = Calendar.getInstance()
        lsXAxis.add("")

        // get current month
        val currentMonth = cal[Calendar.MONTH]
        val currentYear = cal[Calendar.YEAR]
        Log.e(TAG_WEEKPRESENTER, "getStepsByWeek: current month $currentMonth --- current year $currentYear")
        val firstMonday = Utils.getFirstMonday(currentYear, currentMonth)
        Log.e(TAG_WEEKPRESENTER, "getStepsByWeek: first monday  $firstMonday")

//        // lấy step 1 tuần trc
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
        Log.i(TAG_WEEKPRESENTER, "aaaaaaaa -${dateFormat.format(endTime)}")
        Log.i(TAG_WEEKPRESENTER, "aaaaaaaaa -${dateFormat.format(startTime)}")
        Log.i(TAG_WEEKPRESENTER, "getStepsByWeek: ${Date(endTime)}")

        Fitness.getHistoryClient(context, Utils.getAccount(context))
            .readData(Utils.getReadRequestData(startTime, endTime))
            .addOnSuccessListener { response ->
                for (bucket in response.buckets) {
                    //convert days in bucket to milliseconds
                    val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    //convert milliseconds to date
                    val stepsDate = Date(days)
                    // add day vao ls
                    lsXAxis.add(stepsDate.toString().substring(0, 4))
                    Log.e(TAG_WEEKPRESENTER, "accessGoogleFit: $stepsDate")
                    xFloat++
                    for (dataSet in bucket.dataSets) {
                        lsBarEntry.add(
                            BarEntry(
                                xFloat.toFloat(),
                                dumpDataSet(dataSet)
                            )
                        )
                        Log.e(
                            TAG_WEEKPRESENTER,
                            "accessGoogleFit: ${dumpDataSet(dataSet)}"
                        )
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.d(TAG_WEEKPRESENTER, "OnFailure()", e)
            }
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose All your Subscriptions to avoid memory leaks
    }
}