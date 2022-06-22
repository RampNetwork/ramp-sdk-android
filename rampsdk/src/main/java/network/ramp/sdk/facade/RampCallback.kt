package network.ramp.sdk.facade

import network.ramp.sdk.events.model.OffRampPurchase
import network.ramp.sdk.events.model.Purchase

interface RampCallback {

    fun onPurchaseFailed()

    fun onPurchaseCreated(purchase: Purchase, purchaseViewToken: String, apiUrl: String)

    fun onOffRampPurchaseCreated(
        purchase: OffRampPurchase,
        purchaseViewToken: String,
        apiUrl: String
    )

    fun onWidgetClose()

    fun offrampSendCrypto(
        assetSymbol: String,
        amount: String,
        address: String
    )
}