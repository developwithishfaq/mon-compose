package com.monetization.composeviews.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import video.downloader.composeviews.R

@Composable
fun AdLoadingDialog(text: String = "Ad Is Loading") {
    Dialog(
        onDismissRequest = {
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.blackSdk)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.white)
                )
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.white)
                )
            }
        }
    }
}