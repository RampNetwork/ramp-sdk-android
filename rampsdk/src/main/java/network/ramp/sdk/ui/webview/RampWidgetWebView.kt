package network.ramp.sdk.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import network.ramp.sdk.events.RampSdkJsInterface
import network.ramp.sdk.events.RampSdkJsInterface.Companion.RampSdkInterfaceName


internal class RampWidgetWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView(wvClient: WebViewClient, jsInterface: RampSdkJsInterface?) {
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.setSupportMultipleWindows(true)

        webViewClient = wvClient
        jsInterface?.let {
            addJavascriptInterface(it, RampSdkInterfaceName)
        }
        webChromeClient = RampWidgetWebViewChromeClient(context)
    }
}