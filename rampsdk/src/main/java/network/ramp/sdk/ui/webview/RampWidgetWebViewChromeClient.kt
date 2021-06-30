package network.ramp.sdk.ui.webview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import network.ramp.sdk.ui.activity.RampWidgetActivity.Companion.ACTION_VIEW_INTENT


internal class RampWidgetWebViewChromeClient(val context: Context) : WebChromeClient() {

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        when {
            isUserGesture -> {
                val newView = WebView(context)
                newView.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        val intent = if (url.startsWith("intent://"))
                            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        else
                            Intent(ACTION_VIEW_INTENT, Uri.parse(url))
                        context.startActivity(intent)
                    }
                }

                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newView
                resultMsg.sendToTarget()
                return true
            }
            else -> return false
        }
    }
}



