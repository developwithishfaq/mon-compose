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
import com.monetization.composeviews.statefull.nativeAd.SdkNativeViewModel
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo

@Composable
fun rememberNativeAdUiWidget(
    activity: Activity,
    placementKey: String,
    adKey: String,
    adLayout: String,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    adsWidgetData: AdsWidgetData? = null,
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    sdkNativeViewModel: SdkNativeViewModel = viewModel(
        factory = GenericViewModelFactory(SdkNativeViewModel::class.java) {
            SdkNativeViewModel()
        }
    ),
    listener: UiAdsListener? = null,
): AdsUiWidget {
    val lifecycleOwner = LocalSavedStateRegistryOwner.current
    val state by sdkNativeViewModel.state.collectAsState()
    val view = if (state.adWidgetMap[adKey] == null) {
        AdsUiWidget(activity).apply {
            attachWithLifecycle(lifecycle = lifecycleOwner.lifecycle, false)
            setWidgetKey(placementKey, adKey, adsWidgetData, true)
            showNativeAdmob(
                activity = activity,
                adLayout = LayoutInfo.LayoutByName(adLayout),
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
fun SdkNativeAdRefresher(
    activity: Activity,
    placementKey: String,
    adKey: String,
    adLayout: String,
    modifier: Modifier = Modifier,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    adsWidgetData: AdsWidgetData? = null,
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    listener: UiAdsListener? = null,
    sdkNativeViewModel: SdkNativeViewModel = viewModel(
        factory = GenericViewModelFactory(SdkNativeViewModel::class.java) {
            SdkNativeViewModel()
        }
    ),
    widget: AdsUiWidget = rememberNativeAdUiWidget(
        activity = activity,
        placementKey = placementKey,
        adKey = adKey,
        adLayout = adLayout,
        requestNewOnShow = requestNewOnShow,
        showShimmerLayout = showShimmerLayout,
        listener = listener,
        showNewAdEveryTime = showNewAdEveryTime,
        sdkNativeViewModel = sdkNativeViewModel,
        adsWidgetData = adsWidgetData
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
fun SdkNativeAd(
    activity: Activity,
    adLayout: String,
    adKey: String,
    placementKey: String,
    modifier: Modifier = Modifier,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    adsWidgetData: AdsWidgetData? = null,
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    defaultEnable: Boolean = true,
    listener: UiAdsListener? = null,
    sdkNativeViewModel: SdkNativeViewModel = viewModel(
        factory = GenericViewModelFactory(SdkNativeViewModel::class.java) {
            SdkNativeViewModel()
        }
    ),
) {

    val lifecycleOwner = LocalSavedStateRegistryOwner.current
    val state by sdkNativeViewModel.state.collectAsState()
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
            val view = if (state.adWidgetMap[adKey] == null) {
                AdsUiWidget(activity).apply {
                    attachWithLifecycle(lifecycle = lifecycleOwner.lifecycle, false)
                    setWidgetKey(placementKey, adKey, adsWidgetData, defaultEnable)
                    showNativeAdmob(
                        activity = activity,
                        adLayout = LayoutInfo.LayoutByName(adLayout),
                        adKey = adKey,
                        shimmerInfo = showShimmerLayout,
                        oneTimeUse = showNewAdEveryTime,
                        requestNewOnShow = requestNewOnShow,
                        listener = listener
                    )
                }
            } else {
                state.adWidgetMap[adKey]
            }
            view!!
        },
    ) { view ->
//        view.attachWithLifecycle(lifecycle = lifecycle,false)
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

}