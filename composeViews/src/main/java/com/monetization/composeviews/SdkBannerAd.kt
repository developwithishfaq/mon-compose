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
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.bannerads.BannerAdType
import com.monetization.composeviews.statefull.bannerAd.SdkBannerViewModel
import com.monetization.composeviews.statefull.nativeAd.SdkNativeViewModel
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
    sdkNativeViewModel: SdkBannerViewModel = viewModel(
        factory = GenericViewModelFactory(SdkBannerViewModel::class.java) {
            SdkBannerViewModel()
        }
    ),
    listener: UiAdsListener? = null,
): AdsUiWidget {
    val lifecycleOwner = LocalSavedStateRegistryOwner.current
    val state by sdkNativeViewModel.state.collectAsState()
    val view = if (state.adWidgetMap[adKey] == null) {
        AdsUiWidget(activity).apply {
            attachWithLifecycle(lifecycle = lifecycleOwner.lifecycle, true)
            setWidgetKey(placementKey, adKey, null, true)
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
    return view!!
}


@Composable
fun SdkBannerAdRefresher(
    activity: Activity,
    placementKey: String,
    adKey: String,
    adLayout: BannerAdType,
    widget: AdsUiWidget = rememberBannerAdUiWidget(
        activity = activity,
        placementKey = placementKey,
        adKey = adKey,
        bannerAdType = adLayout
    ),
    modifier: Modifier = Modifier,
    sdkNativeViewModel: SdkNativeViewModel = viewModel(
        factory = GenericViewModelFactory(SdkNativeViewModel::class.java) {
            SdkNativeViewModel()
        }
    ),
): AdsUiWidget {
    val lifecycleOwner = LocalSavedStateRegistryOwner.current
    var stateUpdated by rememberSaveable {
        mutableStateOf(false)
    }
    AndroidView(
        modifier = modifier
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
        view.requestLayout()
        if (stateUpdated.not()) {
            logAds("Native Ad on Update View Called, is=${view is AdsUiWidget} View=$view")
            sdkNativeViewModel.updateState(view, adKey)
            stateUpdated = true
        }
    }
    DisposableEffect(Unit) {
        sdkNativeViewModel.setInPause(false, adKey)
        onDispose {
            sdkNativeViewModel.setInPause(true, adKey)
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    sdkNativeViewModel.setInPause(false, adKey)
                }

                Lifecycle.Event.ON_STOP -> {
                    sdkNativeViewModel.setInPause(true, adKey)
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


@Composable
fun SdkBannerAdRefresher(
    activity: Activity,
    adKey: String,
    placementKey: String,
    bannerAdType: BannerAdType,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    showNewAdEveryTime: Boolean = true,
    requestNewOnShow: Boolean = false,
    listener: UiAdsListener? = null,
    sdkBannerViewModel: SdkBannerViewModel = viewModel(
        factory = GenericViewModelFactory(SdkBannerViewModel::class.java) {
            SdkBannerViewModel()
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
        sdkNativeViewModel = sdkBannerViewModel,
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
    return widget
}

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

