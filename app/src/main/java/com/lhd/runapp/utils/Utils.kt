package com.lhd.runapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.lhd.runapp.models.RawData
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object Utils {

    fun dumpDataSet(dataSet: DataSet): Float {
        var totalSteps = 0f
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                totalSteps += dp.getValue(field).asInt()
            }
        }
        return totalSteps
    }

    val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .build()
    }

    val ESTIMATED_STEP_DELTAS: DataSource = DataSource.Builder()
        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .setType(DataSource.TYPE_DERIVED)
        .setStreamName("estimated_steps")
        .setAppPackageName("com.google.android.gms")
        .build()

    fun getReadRequestData(startTime: Long, endTime: Long): DataReadRequest {
        return DataReadRequest.Builder()
            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()
    }


    fun getAccount(context: Context) =
        GoogleSignIn.getAccountForExtension(context, fitnessOptions)


    private fun convertDpToPixels(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * resize image in drawable
     */
    fun resizeBitmap(bitmap: Bitmap, toWidth: Int, toHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            convertDpToPixels(toWidth),
            convertDpToPixels(toHeight),
            false
        )
    }


    /*
    * convert number EX: 1000 -> 1k
    * */
    fun prettyCount(number: Number): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(
                numValue / 10.0.pow((base * 3).toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun getMaxValue(lsBarEntries: ArrayList<BarEntry>): Float {
        var maxValue = 0f
        lsBarEntries.forEachIndexed { _, barEntry ->
            if (barEntry.y > maxValue) {
                maxValue = barEntry.y
            }
        }
        return maxValue
    }

    fun getTimeNow(): Date {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        return Date(cal.timeInMillis)
    }

    fun getNumOfMonth(year: Int, month: Int): Int {
        // lấy số ngày theo tháng
        val yearMonthObject = YearMonth.of(year, month)
        return yearMonthObject.lengthOfMonth()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeRequestToShort(startTime: Long, endTime: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val start = sdf.format(startTime).toString().substring(0, 5)
        val end = sdf.format(endTime).toString().substring(0, 5)
//        return "${start}-${end}"
        return end
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun getData(
        context: Context,
        startTime: Long,
        endTime: Long
    ): ArrayList<RawData> {
        val lsRawData = ArrayList<RawData>()
        val task = Fitness.getHistoryClient(context.applicationContext, getAccount(context))
            .readData(getReadRequestData(startTime, endTime))
        val response = task.await()
        for (bucket in response.buckets) {
            val stepsDate = bucket.getStartTime(TimeUnit.MILLISECONDS)
            var totalDay = 0f
            for (dataSet in bucket.dataSets) {
                // step trong 1 ngay
                totalDay +=
                    dumpDataSet(dataSet)
            }
            lsRawData.add(RawData(stepsDate, totalDay))
        }
        return lsRawData
    }


}