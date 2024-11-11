package com.app.jetpackcomposeads

import android.app.Application
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.listeners.RemoteConfigsProvider
import com.monetization.core.listeners.SdkListener
import com.monetization.core.ui.AdsWidgetData

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        SdkConfigs.setRemoteConfigsListener(object : RemoteConfigsProvider {
            override fun getAdWidgetData(placementKey: String, adKey: String): AdsWidgetData? {
                return null
            }

            override fun isAdEnabled(placementKey: String, adKey: String, adType: AdType): Boolean {
                return true
            }
        })
        SdkConfigs.setListener(
            listener = object : SdkListener {
                override fun canLoadAd(adType: AdType, adKey: String): Boolean {
                    return true
                }

                override fun canShowAd(adType: AdType, adKey: String): Boolean {
                    return true
                }
            },
            testModeEnable = true
        )
    }
}