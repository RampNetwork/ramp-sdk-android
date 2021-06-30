package network.ramp.sdk.facade

import network.ramp.sdk.events.model.Purchase

interface RampCallback {

    fun onPurchaseFailed()

    fun onPurchaseCreated(purchase: Purchase, purchaseViewToken: String, apiUrl: String)

    fun onWidgetClose()
}