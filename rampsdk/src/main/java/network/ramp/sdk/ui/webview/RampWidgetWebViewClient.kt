package network.ramp.sdk.ui.webview

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

internal class RampWidgetWebViewClient(private val progressBar: ProgressBar) : WebViewClient() {

    override fun onPageFinished(view: WebView, url: String) {
        progressBar.visibility = View.GONE
        super.onPageFinished(view, url)
    }
}