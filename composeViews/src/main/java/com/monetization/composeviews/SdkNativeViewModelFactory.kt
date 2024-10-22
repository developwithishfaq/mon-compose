package com.monetization.composeviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GenericViewModelFactory<T : ViewModel>(
    private val viewModelClass: Class<T>,
    private val creator: () -> T
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            return creator() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}