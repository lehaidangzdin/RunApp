package com.lhd.runapp.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lhd.runapp.presenter.HomePresenter
import com.lhd.runapp.presenter.IHomePresenter


class HomeFactory constructor(
    private val application: Application,
    private val iHomePresenter: IHomePresenter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomePresenter(application, iHomePresenter) as T
    }
}