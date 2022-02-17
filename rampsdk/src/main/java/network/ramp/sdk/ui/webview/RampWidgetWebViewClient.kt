package network.ramp.sdk.ui.webview

import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import timber.log.Timber

internal class RampWidgetWebViewClient(private val progressBar: ProgressBar) : WebViewClient() {

    override fun onPageFinished(view: WebView, url: String) {
        progressBar.visibility = View.GONE
        super.onPageFinished(view, url)

        if (url.contains("payment-success.html") or url.contains("payment-failure.html")) {
            view.clearHistory()
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        Timber.d("shouldOverrideUrlLoading request ${request?.url}")
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        Timber.d("shouldOverrideUrlLoading url $url")
        return super.shouldOverrideUrlLoading(view, url)
    }
}