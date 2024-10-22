package com.monetization.composeviews.statefull.nativeAd

import com.monetization.adsmain.widgets.AdsUiWidget

data class SdkNativeState(
    val adWidgetMap: Map<String,AdsUiWidget> = mapOf(),
)