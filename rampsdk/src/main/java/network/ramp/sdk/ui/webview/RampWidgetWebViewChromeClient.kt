package network.ramp.sdk.ui.webview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import network.ramp.sdk.ui.activity.RampWidgetActivity.Companion.ACTION_VIEW_INTENT
import timber.log.Timber


internal class RampWidgetWebViewChromeClient(val activity: Activity) : WebChromeClient() {

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        when {
            isUserGesture -> {
                val newView = WebView(activity)
                newView.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        val intent = if (url.startsWith("intent://"))
                            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        else
                            Intent(ACTION_VIEW_INTENT, Uri.parse(url))
                        activity.startActivity(intent)
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

    override fun onPermissionRequest(request: PermissionRequest) {
        request.resources?.forEach { r ->
            if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                if (!hasCameraPermission()) {
                    requestPermissions()
                    request.deny()
                }
                else
                    request.grant(request.resources)
            }
        }
    }

    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }


    companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
    }
}



