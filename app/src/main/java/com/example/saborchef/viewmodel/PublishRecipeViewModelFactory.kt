package com.example.saborchef.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saborchef.ui.publish.PublishRecipeViewModel

/**
 * Factory para crear instancias de PublishRecipeViewModel con el Application requerido.
 */
class PublishRecipeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublishRecipeViewModel::class.java)) {
            return PublishRecipeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
