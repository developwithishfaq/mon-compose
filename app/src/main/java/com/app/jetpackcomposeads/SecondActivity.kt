package com.app.jetpackcomposeads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.monetization.composeviews.SdkNativeAd
import com.monetization.core.commons.NativeTemplates
import com.monetization.core.counters.CounterManager.isCounterRegistered

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*
            val native = SdkNativeAd(
                activity = this@SecondActivity,
                adLayout = NativeTemplates.SmallNative,
                adKey = "Native",
                placementKey = "Native1"
            )*/
        }
    }
}