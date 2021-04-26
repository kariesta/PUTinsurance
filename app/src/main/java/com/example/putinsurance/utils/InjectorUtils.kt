package com.example.putinsurance.utils

import android.content.Context
import com.example.putinsurance.data.DataRepository
import com.example.putinsurance.viewmodels.TabItemViewModelFactory
import com.example.putinsurance.viewmodels.TabViewModelFactory

object InjectorUtils {

    private fun getSharedPref(context: Context) =
        context.getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)

    fun getDataRepository(context: Context) =
        DataRepository.getInstance(context, getSharedPref(context))

    fun provideTabItemViewModelFactory(context: Context) =
        TabItemViewModelFactory(getDataRepository(context))

    fun provideTabViewModelFactory(context: Context) =
        TabViewModelFactory(getDataRepository(context))
}
