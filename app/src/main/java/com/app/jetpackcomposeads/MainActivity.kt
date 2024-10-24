package com.app.jetpackcomposeads

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.jetpackcomposeads.ui.theme.JetpackComposeAdsTheme
import com.monetization.adsmain.commons.addNewController
import com.monetization.bannerads.AdmobBannerAdsManager
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.composeviews.SdkBanner
import com.monetization.composeviews.SdkNativeAd
import com.monetization.core.commons.NativeTemplates
import com.monetization.nativeads.AdmobNativeAdsManager

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
                    var showSecondNative by remember {
                        mutableStateOf(false)
                    }
                    val banner = SdkBanner(
                        activity = this@MainActivity,
                        bannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
                        adKey = "Banner",
                        placementKey = "Native1"
                    )
                    val native = SdkNativeAd(
                        activity = this@MainActivity,
                        adLayout = NativeTemplates.SmallNative,
                        adKey = "Native",
                        placementKey = "Native1"
                    )
                    if (showSecondNative) {
                        val nativeTwo = SdkBanner(
                            activity = this@MainActivity,
                            bannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
                            adKey = "Banner",
                            placementKey = "Native2"
                        )
                    }
                    Button(onClick = {
//                        showSecondNative = true
//                        native.refreshAd(isNativeAd = true, showShimmerLayout = true)
                        startActivity(Intent(this@MainActivity, SecondActivity::class.java))
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