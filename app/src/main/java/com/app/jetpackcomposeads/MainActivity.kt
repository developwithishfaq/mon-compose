package com.app.jetpackcomposeads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.jetpackcomposeads.ui.theme.JetpackComposeAdsTheme
import com.monetization.adsmain.commons.addNewController
import com.monetization.bannerads.AdmobBannerAdsManager
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.composeviews.SdkBannerAdRefresher
import com.monetization.composeviews.SdkNativeAdRefresher
import com.monetization.core.commons.NativeTemplates
import com.monetization.nativeads.AdmobNativeAdsManager
import com.remote.firebaseconfigs.RemoteCommons.toConfigString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdmobBannerAdsManager.addNewController(
            "Banner", listOf("")
        )
        AdmobNativeAdsManager.addNewController(
            "Native", listOf("")
        )
        setContent {
            JetpackComposeAdsTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val native = SdkNativeAdRefresher(
                        activity = this@MainActivity,
                        adLayout = NativeTemplates.SmallNative,
                        adKey = "Native",
                        placementKey = true.toConfigString()
                    )
                    val adWidget = SdkBannerAdRefresher(
                        activity = this@MainActivity,
                        bannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
                        adKey = "Banner",
                        placementKey = true.toConfigString()
                    )
                    Button(onClick = {
                        native.refreshAd(true, true)
                        adWidget.refreshAd(false, true)
                    }) {
                        Text(text = "Refresh Ad")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposeAdsTheme {
        Greeting("Android")
    }
}