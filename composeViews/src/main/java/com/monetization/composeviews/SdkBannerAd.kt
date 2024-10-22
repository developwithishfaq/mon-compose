package com.monetization.composeviews

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.bannerads.BannerAdType
import com.monetization.composeviews.statefull.bannerAd.SdkBannerViewModel
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.ui.ShimmerInfo

@Composable
fun SdkBannerAd(
    activity: Activity,
    adKey: String,
    placementKey: String,
    bannerAdType: BannerAdType,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    showNewAdEveryTime: Boolean = true,
    requestNewOnShow: Boolean = false,
    defaultEnable: Boolean = true,
    listener: UiAdsListener? = null,
    sdkBannerViewModel: SdkBannerViewModel = viewModel(
        factory = GenericViewModelFactory(SdkBannerViewModel::class.java) {
            SdkBannerViewModel()
        }
    ),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by sdkBannerViewModel.state.collectAsState()

    var stateUpdated by rememberSaveable {
        mutableStateOf(false)
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 2.dp,
                top = 0.dp,
                end = 2.dp,
                bottom = 0.dp
            ),
        factory = {
            val view = if (state.adWidgetMap[adKey] == null) {
                AdsUiWidget(activity).apply {
                    attachWithLifecycle(lifecycle = lifecycleOwner.lifecycle, true)
                    setWidgetKey(placementKey, adKey, null, defaultEnable)
                    showBannerAdmob(
                        activity = activity,
                        bannerAdType = bannerAdType,
                        adKey = adKey,
                        shimmerInfo = showShimmerLayout,
                        oneTimeUse = showNewAdEveryTime,
                        requestNewOnShow = requestNewOnShow,
                        listener = listener,
                    )
                }
            } else {
                state.adWidgetMap[adKey]
            }
            view!!
        },
    ) { view ->
        if (stateUpdated.not()) {
            logAds("Banner Ad on Update View Called, is=${true} View=$view")
            sdkBannerViewModel.updateState(view, adKey)
            stateUpdated = true
        }
    }

    DisposableEffect(Unit) {
        sdkBannerViewModel.setInPause(false, adKey)
        onDispose {
            sdkBannerViewModel.setInPause(true, adKey)
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    sdkBannerViewModel.setInPause(false, adKey)
                }

                Lifecycle.Event.ON_STOP -> {
                    sdkBannerViewModel.setInPause(true, adKey)
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            println("MyComposable: Disposed")
        }
    }


}

