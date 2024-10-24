package com.monetization.composeviews.statefull.nativeAd

import com.monetization.adsmain.widgets.AdsUiWidget

data class SdkNativeState(
    val adPlacements: Map<String,PlacementModel> = mapOf(),
)
data class PlacementModel(
    val widget: AdsUiWidget,
    val adKey: String
)