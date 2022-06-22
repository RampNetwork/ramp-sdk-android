package network.ramp.sdk.facade

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import network.ramp.sdk.BuildConfig
import network.ramp.sdk.events.EventBus
import network.ramp.sdk.events.model.*
import network.ramp.sdk.ui.activity.RampWidgetActivity
import timber.log.Timber

class RampSDK {

    private var callback: RampCallback? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        if (BuildConfig.DEBUG) {
            initLogging()
        }
        handleEvents()
    }

    fun startTransaction(
        activity: Activity,
        config: Config,
        callback: RampCallback,
        url: String? = null
    ) {
        Timber.d("RAMP SDK version - ${BuildConfig.VERSION}")
        release()
        this.callback = callback
        val intent = Intent(activity, RampWidgetActivity::class.java)
        intent.putExtra(
            CONFIG_EXTRA, config
        )
        intent.putExtra(URL_EXTRA, url)
        activity.startActivity(intent)
    }

    fun onOffRampCryptoSent(txHash: String? = null, error: String? = null) {
        scope.launch {
            EventBus.invokeEvent(SendCryptoResult(SendCryptoResultPayload(txHash, error)))
        }
    }

    private fun handleEvents() {
        scope.launch {
            EventBus.events.collectLatest {
                when (it.type) {
                    EventType.WIDGET_CLOSE -> {
                        callback?.onWidgetClose()
                    }
                    EventType.PURCHASE_CREATED -> {
                        val payload = (it as PurchasedCreated).payload
                        callback?.onPurchaseCreated(
                            payload.purchase,
                            payload.purchaseViewToken,
                            payload.apiUrl
                        )
                    }
                    EventType.PURCHASE_FAILED -> {
                        callback?.onPurchaseFailed()
                    }

                    EventType.OFFRAMP_PURCHASE_CREATED -> {
                        val payload = (it as OffRampPurchaseCreated).payload
                        callback?.onOffRampPurchaseCreated(
                            payload.purchase,
                            payload.purchaseViewToken,
                            payload.apiUrl
                        )
                    }

                    EventType.SEND_CRYPTO -> {
                        val payload = (it as SendCrypto).payload
                        Timber.d("SEND CRYPTO : ${payload.address} ${payload.amount} ${payload.assetSymbol}")
                        callback?.offrampSendCrypto(
                            assetSymbol = payload.assetSymbol,
                            amount = payload.amount,
                            address = payload.assetSymbol
                        )
                    }
                    else -> {
                        Timber.w("Unhandled Event")
                    }
                }
            }
        }
    }

    private fun release() {
        callback = null
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        internal const val CONFIG_EXTRA = "config"
        internal const val URL_EXTRA = "url"
    }
}