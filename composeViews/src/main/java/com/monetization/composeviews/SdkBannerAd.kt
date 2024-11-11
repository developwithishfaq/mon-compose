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
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.bannerads.BannerAdType
import com.monetization.composeviews.statefull.nativeAd.OnScreenAdsViewModel
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.ui.ShimmerInfo


@Composable
fun rememberBannerAdUiWidget(
    activity: Activity,
    placementKey: String,
    adKey: String,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    bannerAdType: BannerAdType,
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    onScreenAdsViewModel: OnScreenAdsViewModel = viewModel(
        factory = GenericViewModelFactory(OnScreenAdsViewModel::class.java) {
            OnScreenAdsViewModel()
        }
    ),
    listener: UiAdsListener? = null,
): AdsUiWidget {
    val lifecycleOwner = LocalSavedStateRegistryOwner.current
    val state by onScreenAdsViewModel.state.collectAsState()
    val view = if (state.adPlacements[placementKey] == null) {
        AdsUiWidget(activity).apply {
            attachWithLifecycle(
                lifecycle = lifecycleOwner.lifecycle,
                forBanner = true,
                isJetpackCompose = true
            )
            setWidgetKey(placementKey = placementKey, adKey =  adKey, isNativeAd = false, null, true)
            showBannerAdmob(
                activity = activity,
                bannerAdType = bannerAdType,
                adKey = adKey,
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable
            )
        }
    } else {
        state.adPlacements[placementKey]?.widget
    }
    return view!!
}


@Composable
fun SdkBanner(
    activity: Activity,
    adKey: String,
    placementKey: String,
    bannerAdType: BannerAdType,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    showNewAdEveryTime: Boolean = true,
    requestNewOnShow: Boolean = false,
    listener: UiAdsListener? = null,
    sdkBannerViewModel: OnScreenAdsViewModel = viewModel(
        factory = GenericViewModelFactory(OnScreenAdsViewModel::class.java) {
            OnScreenAdsViewModel()
        }
    ),
    widget: AdsUiWidget = rememberBannerAdUiWidget(
        activity = activity,
        placementKey = placementKey,
        adKey = adKey,
        bannerAdType = bannerAdType,
        requestNewOnShow = requestNewOnShow,
        showShimmerLayout = showShimmerLayout,
        listener = listener,
        showNewAdEveryTime = showNewAdEveryTime,
        onScreenAdsViewModel = sdkBannerViewModel,
    ),
): AdsUiWidget {
    val lifecycleOwner = LocalSavedStateRegistryOwner.current
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
            widget
        },
    ) { view ->
        if (stateUpdated.not()) {
            logAds("Banner Ad on Update View Called, is=${true} View=$view")
            sdkBannerViewModel.updateState(view, placementKey, adKey)
            stateUpdated = true
        }
    }

    DisposableEffect(Unit) {
        sdkBannerViewModel.setInPause(false, placementKey, true)
        onDispose {
            sdkBannerViewModel.setInPause(true, placementKey, true)
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    sdkBannerViewModel.setInPause(false, placementKey, true)
                }

                Lifecycle.Event.ON_STOP -> {
                    sdkBannerViewModel.setInPause(true, placementKey, true)
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
    return widget
}

