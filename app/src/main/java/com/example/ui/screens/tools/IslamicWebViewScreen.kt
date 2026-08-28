package com.example.ui.screens.tools

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun IslamicWebViewScreen(
    title: String,
    url: String,
    onBack: () -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        val webView = webViewRef
        if (webView?.canGoBack() == true) webView.goBack() else onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val webView = webViewRef
                if (webView?.canGoBack() == true) webView.goBack() else onBack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewRef = this
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.builtInZoomControls = false
                    settings.displayZoomControls = false
                    settings.loadsImagesAutomatically = true
                    loadUrl(url)
                }
            },
            update = { webViewRef = it }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.apply {
                stopLoading()
                webViewClient = null
                webChromeClient = null
                destroy()
            }
            webViewRef = null
        }
    }
}
