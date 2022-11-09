package com.lhd.runapp.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.lhd.runapp.FitRequestCode
import com.lhd.runapp.fragment.TAG

class FragmentDayPresenter(application: Application) : AndroidViewModel(application) {

}