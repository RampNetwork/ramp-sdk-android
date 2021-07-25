package network.ramp.sdk.facade

import android.app.Activity
import android.content.Context
import android.content.Intent
import network.ramp.sdk.BuildConfig
import network.ramp.sdk.events.model.PurchaseCreatedPayload
import network.ramp.sdk.events.model.PurchasedFailed
import network.ramp.sdk.events.model.WidgetClose
import network.ramp.sdk.ui.activity.RampWidgetActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class RampSDK {

    private var callback: RampCallback? = null

    init {
        if (BuildConfig.DEBUG) {
            initLogging()
        }
    }

    fun startTransaction(activity: Activity, config: Config, callback: RampCallback) {
        release()
        this.callback = callback
        EventBus.getDefault().register(this)
        val intent = Intent(activity, RampWidgetActivity::class.java)
        intent.putExtra(
            CONFIG_EXTRA, config
        )
        activity.startActivity(intent)
    }

    private fun release() {
        callback = null
        EventBus.getDefault().unregister(this)
        EventBus.clearCaches()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onEvent(payload: PurchaseCreatedPayload) {
        callback?.onPurchaseCreated(payload.purchase, payload.purchaseViewToken, payload.apiUrl)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onPurchaseFailed(event: PurchasedFailed) {
        callback?.onPurchaseFailed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onWidgetClosed(event: WidgetClose) {
        callback?.onWidgetClose()
        release()
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        internal const val CONFIG_EXTRA = "config"
    }
}