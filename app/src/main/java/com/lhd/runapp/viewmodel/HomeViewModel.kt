package com.lhd.runapp.viewmodel

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
import com.lhd.runapp.models.DataChart
import com.lhd.runapp.models.RawData
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils
import com.lhd.runapp.utils.Utils.fitnessOptions
import com.lhd.runapp.utils.Utils.getAccount
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val TAG = "HomePresenter"

class HomePresenter(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val cal = Calendar.getInstance()
    private val currentYear = cal[Calendar.YEAR]

    //
    val totalSteps = ObservableField<Int>()
    val process = MutableLiveData<Float>()
    val dataChartByWeek = MutableLiveData<DataChart>()
    val dataChartByWeekOfMonth = MutableLiveData<DataChart>()
    val dataChartByMonth = MutableLiveData<DataChart>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission(activity: Activity) {
        totalSteps.set(0)
        process.value = 0f
        if (!GoogleSignIn.hasPermissions(getAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getAccount(context),
                fitnessOptions
            )
        } else {
            getStepsByDayOfWeek()
        }
    }


    @SuppressLint("SimpleDateFormat")
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByDayOfWeek() {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
//        lấy step 1 tuần trc
        cal.add(Calendar.DAY_OF_WEEK, -6)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTime = cal.timeInMillis

        cal.time = Date()
        cal[Calendar.DAY_OF_WEEK] += 1
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val endTime = cal.timeInMillis

        Log.e(
            TAG,
            "getStepsByDayOfWeek: start time : ${sdf.format(startTime)} --- end time : ${
                sdf.format(endTime)
            }"
        )

        GlobalScope.launch(Dispatchers.Main) {
            val rawData = async { Utils.getData(context, startTime, endTime) }
            handleRawDataByDayOfWeek(rawData.await())
        }
    }

    private fun handleRawDataByDayOfWeek(lsRawData: ArrayList<RawData>) {
        val lsXAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()
        lsXAxis.add("")
        process.value = lsRawData[lsRawData.size - 2].step
        totalSteps.set(lsRawData[lsRawData.size - 2].step.toInt())
        lsRawData.forEachIndexed { index, rawData ->
            val time = Date(rawData.time)
            lsXAxis.add(time.toString().substring(0, 3))
            lsBarEntry.add(BarEntry(index + 1f, rawData.step))
        }
        dataChartByWeek.postValue(DataChart(lsXAxis, lsBarEntry))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getStepsByMonth() {
        val daysInMonth = Utils.getNumOfMonth(currentYear, 12)

        cal[Calendar.DATE] = 1
        cal[Calendar.MONTH] = 0
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTimeReadRequest = cal.timeInMillis
        // end time
        cal[Calendar.DATE] = daysInMonth + 1
        cal[Calendar.MONTH] = 11
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val endTimeReadRequest = cal.timeInMillis

        GlobalScope.launch(Dispatchers.Main) {
            val rawData = async { Utils.getData(context, startTimeReadRequest, endTimeReadRequest) }
            handleRawDataByMonth(rawData.await())
        }

    }

    private fun handleRawDataByMonth(lsRawData: ArrayList<RawData>) {
        val lsAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()
        lsAxis.add("")
        var start = 0
        var end = 0
        for (i in 1..12) {
            val dayOfMonth = Utils.getNumOfMonth(currentYear, i)
            var totalMonth = 0f
            val time = Date(lsRawData[end].time).toString().substring(4, 7)
            end += dayOfMonth
            if (end >= lsRawData.size) {
                end = lsRawData.size - 1
            }
            for (j in start..end) {
                totalMonth += lsRawData[j].step
            }
            lsAxis.add(time)
            lsBarEntry.add(BarEntry(i.toFloat(), totalMonth))
            dataChartByMonth.value = DataChart(lsAxis, lsBarEntry)
            start = end + 1
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByWeekOfMonth() {
        val currentMonth = cal[Calendar.MONTH]
        cal[Calendar.DATE] = 1
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTime = cal.timeInMillis
        // end time
        val day = Utils.getNumOfMonth(cal[Calendar.YEAR], currentMonth)

        cal[Calendar.DATE] = day
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val endTime = cal.timeInMillis


        GlobalScope.launch(Dispatchers.Main) {
            try {
                val lsRawData = async { Utils.getData(context, startTime, endTime) }
                handlerRawDataByWeekOfMonth(lsRawData.await())
            } catch (e: Exception) {
                Log.e(TAG, "getStepsByWeek: err :${e}")
            }
        }
    }

    private fun handlerRawDataByWeekOfMonth(lsRawData: ArrayList<RawData>) {
        val lsAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()
        lsAxis.add("")

        var day = 6
        var start = 0
        for (i in 0..4) {
            var totalWeek = 0f
            if (day >= lsRawData.size) {
                day = lsRawData.size - 1
            }
            for (j in start..day) {
                totalWeek += lsRawData[j].step
            }
            val time = Utils.convertTimeRequestToShort(lsRawData[start].time, lsRawData[day].time)
            Log.e(TAG, "handlerRawDataByWeekOfMonth: time $time")
            lsAxis.add(time)
//            lsAxis.add((i + 1).toString())
            lsBarEntry.add(BarEntry(i + 2f, totalWeek))
            dataChartByWeekOfMonth.postValue(DataChart(lsAxis, lsBarEntry))

            start = day + 1
            day += 7

        }
    }


}