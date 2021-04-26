package com.example.putinsurance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.data.DataRepository
import com.example.putinsurance.ui.main.StateViewModel

object InjectorUtils {

    private fun getSharedPref(context: Context) = context.getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)

    fun getDataRepository(context: Context) : DataRepository {
        return DataRepository.getInstance(context, getSharedPref(context))
    }

    fun provideTabItemViewModelFactory(context: Context):  TabItemViewModelFactory {

        val dataRepository = getDataRepository(context)
        return TabItemViewModelFactory(dataRepository)
    }
}

class TabItemViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StateViewModel(dataRepository) as T
    }
}