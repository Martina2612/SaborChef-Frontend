package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saborchef.data.DataStoreManager

class HomeViewModelFactory(
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
