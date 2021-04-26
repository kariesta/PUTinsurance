package com.example.putinsurance.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.data.DataRepository
import com.example.putinsurance.ui.main.StateViewModel

class TabItemViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StateViewModel(dataRepository) as T
    }
}