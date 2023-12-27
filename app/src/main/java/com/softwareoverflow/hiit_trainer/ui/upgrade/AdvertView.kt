package com.softwareoverflow.hiit_trainer.ui.upgrade

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.ui.theme.AppTheme
import timber.log.Timber

@Composable
fun AdvertView(modifier: Modifier = Modifier, onAdLoaded:() -> Unit) {
    val isUpgraded by  UpgradeManager.userUpgradedFlow.collectAsState()

    if(!isUpgraded) {
        val isInEditMode = LocalInspectionMode.current
        if (isInEditMode) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(horizontal = 2.dp, vertical = 6.dp),
                textAlign = TextAlign.Center,
                text = "Advert Here",
            )
        } else {
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = {context ->

                    val ad = AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = context.getString(R.string.ad_id_banner)

                        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                        val builder = AdRequest.Builder().apply {
                            val personalizedAds = prefs.getBoolean(
                                context.getString(R.string.personalized_ads_enabled),
                                false
                            )

                            // TODO remove this with the new consent manager!
                            if (!personalizedAds) {
                                val bundle = Bundle()
                                bundle.putString("npa", "1")
                                addNetworkExtrasBundle(AdMobAdapter::class.java, bundle)
                            }

                            adListener = object : AdListener() {
                                override fun onAdFailedToLoad(p0: LoadAdError) {
                                    Timber.w("Failed to load banner advert. ${p0.message}")
                                    super.onAdFailedToLoad(p0)
                                }

                                override fun onAdLoaded() {
                                    onAdLoaded()
                                    super.onAdLoaded()
                                }
                            }
                        }
                        loadAd(builder.build())
                    }

                    MobileAdsManager.setBannerAd(ad)

                    return@AndroidView ad
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdvertPreview() {

    AppTheme {
        AdvertView(){}
        Column(Modifier.fillMaxSize()){

        }
    }

}