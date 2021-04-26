package com.example.putinsurance.utils

import android.content.Context
import com.example.putinsurance.data.DataRepository
import com.example.putinsurance.viewmodels.TabItemViewModelFactory

object InjectorUtils {

    private fun getSharedPref(context: Context) =
        context.getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)

    fun getDataRepository(context: Context) =
        DataRepository.getInstance(context, getSharedPref(context))

    fun provideTabItemViewModelFactory(context: Context) =
        TabItemViewModelFactory(getDataRepository(context))
}
