package network.ramp.sdk.events

import android.webkit.JavascriptInterface

internal class RampSdkJsInterface(
    val onPostMessage: (payloadJson: String) -> Unit
) {

    @JavascriptInterface
    @Suppress("unused")
    fun postMessage(payloadJson: String) {
        onPostMessage(payloadJson)
    }

    companion object {
        internal const val RampSdkInterfaceName = "RampInstantMobile"
    }
}