package network.ramp.sdk.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import network.ramp.sdk.events.RampSdkJsInterface
import network.ramp.sdk.events.RampSdkJsInterface.Companion.RampSdkInterfaceName
import network.ramp.sdk.ui.activity.RampWidgetActivity


internal class RampWidgetWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView(
        activity: RampWidgetActivity,
        wvClient: WebViewClient,
        jsInterface: RampSdkJsInterface?,
        fileChooserLauncher: ActivityResultLauncher<Intent>,
        onFilePathCallback: (ValueCallback<Array<Uri>>?) -> Unit
    ) {
        webChromeClient = RampWidgetWebViewChromeClient(activity, fileChooserLauncher, onFilePathCallback)
        webViewClient = wvClient
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.allowContentAccess = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)

        settings.mediaPlaybackRequiresUserGesture = false
        jsInterface?.let {
            addJavascriptInterface(it, RampSdkInterfaceName)
        }
    }
}