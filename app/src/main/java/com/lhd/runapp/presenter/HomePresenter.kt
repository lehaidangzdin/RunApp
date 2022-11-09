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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.lhd.runapp.fragment.TAG
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.Utils.fitnessOptions

class HomePresenter(
    application: Application,
    private val iHomePresenter: IHomePresenter
) : AndroidViewModel(application) {

    val totalSteps = ObservableField<Int>()
    val process = MutableLiveData<Float>()

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private fun getGoogleAccount() =
        GoogleSignIn.getAccountForExtension(context.applicationContext, fitnessOptions)

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission(activity: Activity) {
        val isSignIn = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)
        if (!isSignIn) {
            totalSteps.set(0)
            process.value = 0f
            Log.e(TAG, "checkPermission: ${FitRequestCode.GG_FIT_REQUEST_CODE.ordinal}")
            GoogleSignIn.requestPermissions(
                activity,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getGoogleAccount(),
                fitnessOptions
            )
        } else {
            iHomePresenter.updateUi()
            recordingClient()
            getStepsByCurrentDay()
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

    private fun getStepsByCurrentDay() {
        Fitness.getHistoryClient(context.applicationContext, getGoogleAccount())
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = when {
                    dataSet.isEmpty -> 0
                    else -> dataSet.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                totalSteps.set(total)
                process.value = total.toFloat()
//                Log.i(TAG, "Total steps: $total")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem getting the step count.", e)
            }
    }


    override fun onCleared() {
        super.onCleared()
        // Dispose All your Subscriptions to avoid memory leaks
    }

}