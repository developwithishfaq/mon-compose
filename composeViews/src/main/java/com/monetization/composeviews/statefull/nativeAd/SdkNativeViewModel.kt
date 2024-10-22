package com.monetization.composeviews.statefull.nativeAd

import android.util.Log
import androidx.lifecycle.ViewModel
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.core.commons.AdsCommons.logAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SdkNativeViewModel : ViewModel() {

    private val _state = MutableStateFlow(SdkNativeState())
    val state = _state.asStateFlow()

    init {
        Log.d("cvrr", "SdkNativeViewModel")
    }

    fun updateState(widget: AdsUiWidget, adKey: String) {
        val mapp = state.value.adWidgetMap.toMutableMap()
        mapp[adKey] = widget
        _state.update {
            it.copy(
                adWidgetMap = mapp
            )
        }
    }

    fun destroyAdByKey(adKey: String, removeIfShown: Boolean = true) {
        val mapp = state.value.adWidgetMap.toMutableMap()
        val oldSize = mapp.size
        if (removeIfShown) {
            if (mapp[adKey]?.isAdPopulated(true) == true) {
                mapp.remove(adKey)
            }
        } else {
            mapp.remove(adKey)
        }
        logAds("Native Widgets Size: old=${oldSize},new=${mapp.size}")
        _state.update {
            it.copy(
                adWidgetMap = mapp
            )
        }
    }

    fun setInPause(check: Boolean, key: String) {
        val mapp = state.value.adWidgetMap.toMutableMap()
        mapp[key]?.setInPause(check, false)
        _state.update {
            it.copy(
                adWidgetMap = mapp
            )
        }
    }


}