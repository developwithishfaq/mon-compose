package com.monetization.composeviews.statefull.bannerAd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SdkBannerViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SdkBannerViewModel::class.java)) {
            return SdkBannerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
