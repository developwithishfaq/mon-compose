package com.monetization.composeviews.statefull.nativeAd

import android.util.Log
import androidx.lifecycle.ViewModel
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.core.commons.AdsCommons.logAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnScreenAdsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SdkNativeState())
    val state = _state.asStateFlow()

    init {
        Log.d("cvrr", "SdkNativeViewModel")
    }

    fun updateState(widget: AdsUiWidget, placementKey: String, adKey: String) {
        val mapp = state.value.adPlacements.toMutableMap()
        mapp[placementKey] = PlacementModel(
            widget,
            adKey
        )
        _state.update {
            it.copy(
                adPlacements = mapp
            )
        }
    }

    fun destroyAdByKey(placementKey: String, removeIfShown: Boolean = true) {
        val mapp = state.value.adPlacements.toMutableMap()
        val oldSize = mapp.size
        if (removeIfShown) {
            if (mapp[placementKey]?.widget?.isAdPopulated(true) == true) {
                mapp.remove(placementKey)
            }
        } else {
            mapp.remove(placementKey)
        }
        logAds("Native Widgets Size: old=${oldSize},new=${mapp.size}")
        _state.update {
            it.copy(
                adPlacements = mapp
            )
        }
    }

    fun setInPause(check: Boolean, placementKey: String, forBannerAd: Boolean) {
        val mapp = state.value.adPlacements.toMutableMap()
        mapp[placementKey]?.widget?.setInPause(check, forBannerAd)
        _state.update {
            it.copy(
                adPlacements = mapp
            )
        }
    }


}