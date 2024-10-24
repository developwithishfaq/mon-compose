package com.monetization.composeviews.statefull.bannerAd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monetization.composeviews.statefull.nativeAd.OnScreenAdsViewModel

class SdkBannerViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnScreenAdsViewModel::class.java)) {
            return OnScreenAdsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
