package com.lhd.runapp.presenter

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.fitness.Fitness
import com.lhd.runapp.models.DataChart
import com.lhd.runapp.utils.Utils.dumpDataSet
import com.lhd.runapp.utils.Utils.getAccount
import com.lhd.runapp.utils.Utils.getReadRequestData
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
class MonthPresenter(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val dataChar = MutableLiveData<DataChart>()

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd/MM/yyyy")
    private val cal = Calendar.getInstance()
    private val currentYear = cal[Calendar.YEAR]
    private val currentMonth = cal[Calendar.MONTH] + 1


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByMonth() {
        val lsXAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()
        lsXAxis.add("")

        for (i in 1..currentMonth) {
            var totalMonth = 0
            // lấy số ngày theo tháng
            val yearMonthObject = YearMonth.of(currentYear, i);
            val daysInMonth = yearMonthObject.lengthOfMonth()
            Log.e(TAG, "getStepsByMonth:  day in month $daysInMonth")

            // set starttime = 01/01/{năm hiện tại}
            val startDate = "01/${i}/${currentYear}"
            cal[Calendar.DATE] = 1
            cal[Calendar.MONTH] = i
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal.time = sdf.parse(startDate)!!
            val startTimeReadRequest = cal.timeInMillis
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
            val endTimeReadRequest = cal.timeInMillis

            // loge time
            val dateFormat: DateFormat = DateFormat.getDateInstance()
            Log.i(com.lhd.runapp.TAG, "start time: ${dateFormat.format(startTimeReadRequest)}")
            Log.i(com.lhd.runapp.TAG, "end time: ${dateFormat.format(endTimeReadRequest)}")

            // add month  vao ls
            Fitness.getHistoryClient(context.applicationContext, getAccount(context))
                .readData(getReadRequestData(startTimeReadRequest, endTimeReadRequest))
                .addOnSuccessListener { response ->
                    // 1 thang
                    //convert milliseconds to date
                    val stepsDate = Date(response.buckets[0].getStartTime(TimeUnit.MILLISECONDS))
                    for (bucket in response.buckets) {
                        var total = 0
//                        step trong 1 ngay
                        for (dataSet in bucket.dataSets) {
                            total +=
                                dumpDataSet(dataSet)
                        }
                        totalMonth += total
                    }
                    Log.e(
                        TAG,
                        "getStepsByMonth: end time ${stepsDate.toString().substring(4, 7)}"
                    )
                    lsXAxis.add(stepsDate.toString().substring(4, 7))
                    lsBarEntry.add(BarEntry(i.toFloat(), totalMonth.toFloat()))
                    dataChar.value = DataChart(lsXAxis, lsBarEntry)
                }
                .addOnFailureListener { e -> Log.d(com.lhd.runapp.TAG, "OnFailure()", e) }
//
        }
    }

}