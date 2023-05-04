package network.ramp.sdk.facade

import network.ramp.sdk.events.model.OfframpSale
import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.events.model.Asset

interface RampCallback {

    @Deprecated(message = "This method is deprecated and will be removed in future versions.")
    fun onPurchaseFailed()

    fun onPurchaseCreated(purchase: Purchase, purchaseViewToken: String, apiUrl: String)

    fun onOfframpSaleCreated(
        sale: OfframpSale,
        saleViewToken: String,
        apiUrl: String
    )

    fun onWidgetClose()

    fun offrampSendCrypto(
        assetInfo: Asset,
        amount: String,
        address: String
    )
}