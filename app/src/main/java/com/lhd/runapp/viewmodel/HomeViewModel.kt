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
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.lhd.runapp.R
import com.lhd.runapp.models.Challenger
import com.lhd.runapp.models.DataChart
import com.lhd.runapp.models.RawData
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils
import com.lhd.runapp.utils.Utils.MAX_MONTH
import com.lhd.runapp.utils.Utils.fitnessOptions
import com.lhd.runapp.utils.Utils.getAccount
import com.lhd.runapp.utils.Utils.getDailySteps
import com.lhd.runapp.utils.Utils.getNameOfMonth
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

const val TAG = "HomePresenter"

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val cal = Calendar.getInstance()

    val countTitles = ObservableField(0)
    val process = MutableLiveData(0f)
    val dataChartByWeek = MutableLiveData<DataChart>()
    val dataChartByWeekOfMonth = MutableLiveData<DataChart>()
    val dataChartByMonth = MutableLiveData<DataChart>()
    val lsMonthChallenger = MutableLiveData<ArrayList<Challenger>>()
    val isSignIn = MutableLiveData(false)

    fun setIsSignIn(isSignIn: Boolean) {
        this.isSignIn.value = isSignIn
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission(activity: Activity) {
        if (!GoogleSignIn.hasPermissions(getAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getAccount(context),
                fitnessOptions
            )
        } else {
            setIsSignIn(true)
            getStepDaily()
        }
    }

    @SuppressLint("SimpleDateFormat")
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByDayOfWeek() {
        val cal = Calendar.getInstance()

        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal.time = Date()
        val endTime = cal.timeInMillis

//        lấy step 6 ngày trc -> hôm nay
        cal.add(Calendar.DAY_OF_WEEK, -6)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTime = cal.timeInMillis

        // lấy dữ liệu dùng coroutines xử lí bất đồng bộ khi lấy dữ liệu
        GlobalScope.launch(Dispatchers.Main) {
            val rawData = async { Utils.getData(context, startTime, endTime) }
            handleRawDataByDayOfWeek(rawData.await())
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getStepDaily() {
        GlobalScope.launch(Dispatchers.Main) {
            val steps = async { getDailySteps(context) }
            process.value = (steps.await()).toFloat()
        }
    }

    private fun handleRawDataByDayOfWeek(lsRawData: ArrayList<RawData>) {
        val lsXAxis = ArrayList<String>()
        val lsBarEntry = ArrayList<BarEntry>()
        lsRawData.forEachIndexed { index, rawData ->
            val time = Date(rawData.time)
            lsXAxis.add(time.toString().substring(0, 3))
            lsBarEntry.add(BarEntry(index.toFloat(), rawData.step))
        }

        dataChartByWeek.postValue(DataChart(lsXAxis, lsBarEntry))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getStepsByMonth() {
        countTitles.set(0)
        val cal = Calendar.getInstance()

        val daysInMonth = Utils.getNumOfMonth(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1)
        // lấy thời gian
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
        val lsChallenger = ArrayList<Challenger>()
//        lsAxis.add("")
        var start = 0
        var end = 0
        // loop 12 tháng để tính tổng steps từng tháng
        for (i in 1..12) {
            val dayOfMonth = Utils.getNumOfMonth(cal[Calendar.YEAR], i)
            var totalMonth = 0f
            val time = Date(lsRawData[end].time).toString().substring(4, 7)
            end += dayOfMonth
            if (end >= lsRawData.size) {
                end = lsRawData.size - 1
            }
            var day: Long = 0
            // tính tổng số bước trong 1 tháng
            for (j in start..end) {
                totalMonth += lsRawData[j].step
                if (totalMonth >= MAX_MONTH && day == 0L) {
                    day = lsRawData[j].time
                }
            }

            if (day != 0L) {
                countTitles.set(countTitles.get()!! + 1)
            }
            // add vào list
            lsAxis.add(time)
            lsBarEntry.add(BarEntry((i - 1).toFloat(), totalMonth))
            // add challenger
            val icon = R.drawable.huy_huong1
            val title = "${getNameOfMonth(i).toString()} Challenger"

            lsChallenger.add(
                Challenger(
                    icon,
                    title,
                    day,
                    totalMonth.toInt().toString(),
                    MAX_MONTH.toString()
                )
            )
            //
            start = end + 1
        }
        this.lsMonthChallenger.postValue(lsChallenger)
        dataChartByMonth.value = DataChart(lsAxis, lsBarEntry)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStepsByWeekOfMonth() {
        // lấy thời gian
        val cal = Calendar.getInstance()

        val day = Utils.getNumOfMonth(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1)
        Log.e(TAG, "getStepsByWeekOfMonth: $day")
        cal[Calendar.DATE] = day
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val endTime = cal.timeInMillis
        // start time
        cal[Calendar.DATE] = 1
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTime = cal.timeInMillis


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

        var day = 6
        var start = 0
        // loop 1 tháng -> tổng từng tuần
        for (i in 0..4) {
            var totalWeek = 0f
            if (day >= lsRawData.size) {
                day = lsRawData.size - 1
            }
            // tỉnh tổng steps từng tuần
            for (j in start..day) {
                totalWeek += lsRawData[j].step
            }
            val time = Utils.convertTimeRequestToShort(lsRawData[start].time, lsRawData[day].time)
            lsAxis.add(time)
            lsBarEntry.add(BarEntry((i).toFloat(), totalWeek))

            start = day + 1
            day += 7
        }
        dataChartByWeekOfMonth.postValue(DataChart(lsAxis, lsBarEntry))
    }

    fun subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(context, getAccount(context))
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed!")
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }
}