package com.lhd.runapp.presenter

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.fitness.Fitness
import com.lhd.runapp.models.DataChart
import com.lhd.runapp.utils.Utils.dumpDataSet
import com.lhd.runapp.utils.Utils.getAccount
import com.lhd.runapp.utils.Utils.getReadRequestData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
class MonthPresenter(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val dataChar = MutableLiveData<DataChart>()
    private val lsAxis = ObservableArrayList<String>()
    private val lsBarEntry = ObservableArrayList<BarEntry>()

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd/MM/yyyy")
    private val cal = Calendar.getInstance()
    private val currentYear = cal[Calendar.YEAR]
    private val currentMonth = cal[Calendar.MONTH] + 1

    init {
        lsAxis.add("")

    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByMonth() {
        for (i in 1..12) {

            // lấy số ngày theo tháng
            val yearMonthObject = YearMonth.of(currentYear, i)
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
            Log.i(TAG, "start time: ${dateFormat.format(startTimeReadRequest)}")
            Log.i(TAG, "end time: ${dateFormat.format(endTimeReadRequest)}")

            getTotalStepByMonthThenAddToList(
                startTimeReadRequest,
                endTimeReadRequest,
                i
            )
            // chờ get thông tin tháng xong r sang tháng tiếp theo
            Thread.sleep(10)
        }
    }

    private fun getTotalStepByMonthThenAddToList(
        startTimeReadRequest: Long,
        endTimeReadRequest: Long,
        position: Int
    ) {
        // add month  vao ls
        var totalMonth = 0f
        Fitness.getHistoryClient(context.applicationContext, getAccount(context))
            .readData(getReadRequestData(startTimeReadRequest, endTimeReadRequest))
            .addOnSuccessListener { response ->
                // 1 thang
                //convert milliseconds to date
                val stepsDate = Date(response.buckets[0].getStartTime(TimeUnit.MILLISECONDS))
                for (bucket in response.buckets) {
                    // step trong 1 ngay
                    var totalDay = 0f
                    for (dataSet in bucket.dataSets) {
                        totalDay +=
                            dumpDataSet(dataSet)
                    }
                    totalMonth += totalDay
                }

                Log.e(TAG, "getStepsByMonth: ${stepsDate.toString().substring(4, 7)}")
                lsAxis.add(stepsDate.toString().substring(4, 7))
                lsBarEntry.add(BarEntry(position.toFloat(), totalMonth))
                dataChar.value = DataChart(lsAxis, lsBarEntry)
            }
            .addOnFailureListener { e -> Log.d(com.lhd.runapp.TAG, "OnFailure()", e) }
    }


}