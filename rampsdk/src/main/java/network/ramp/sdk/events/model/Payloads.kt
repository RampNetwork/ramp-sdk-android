package network.ramp.sdk.events.model


internal data class OpenLinkPayload(val linkType: String, val url: String)


data class WidgetClosePayload(
    val showAlert: Boolean,
    val descriptionText: String? = null,
    val acceptText: String? = null,
    val rejectText: String? = null
)

internal data class KycInitPayload(
    val email: String,
    val countryCode: String,
    val verificationId: Int,
    val metaData: String?,
    val apiKey: String,
    val provider: String
)

internal data class KycStartedPayload(var verificationId: Int = 0)

internal data class KycFinishedPayload(
    var verificationId: Int = 0,
    var identityAccessKey: String = ""
)

internal data class KycAbortedPayload(var verificationId: Int = 0)

internal data class KycSubmittedPayload(
    var verificationId: Int = 0,
    var identityAccessKey: String = ""
)

internal data class KycErrorPayload(var verificationId: Int = 0)


data class PurchaseCreatedPayload(
    val purchase: Purchase,
    val purchaseViewToken: String,
    val apiUrl: String
)

data class Purchase(
    val endTime: String, // purchase validity time, ISO date-time string
    val escrowAddress: String? = null, // filled only for escrow-backend purchases
    val cryptoAmount: String, // number-string, in wei or token units
    val fiatCurrency: String, // three-letter currency code
    val fiatValue: Long, // total value the user pays for the purchase, in fiatCurrency
    val assetExchangeRateEur: Double, // price of 1 whole token of purchased asset, in fiatCurrency
    val fiatExchangeRateEur: Long,
    val baseRampFee: Double, // base Ramp fee before any modifications, in fiatCurrency
    val networkFee: Double, // network fee for transferring the purchased asset, in fiatCurrency
    val appliedFee: Double, // final fee the user pays (included in fiatValue), in fiatCurrency
    val createdAt: String, // ISO date-time string
    val updatedAt: String, // ISO date-time string
    val id: String,
    val asset: Asset, // description of the purchased asset (address, symbol, name, decimals)
    val receiverAddress: String, // blockchain address of the buyer
    val assetExchangeRate: Double,
    val purchaseViewToken: String,
    val actions: List<Action>,
    val status: String, // purchase status
    val paymentMethodType: String // type of payment method used to pay for the swap: 'MANUAL_BANK_TRANSFER' | 'AUTO_BANK_TRANSFER' | 'CARD_PAYMENT' | 'APPLE_PAY'
)

data class Action(
    val newStatus: String,
    val timestamp: String
)

data class Asset(
    val address: String? = null, // 0x-prefixed address for ERC-20 tokens, `null` for ETH
    val symbol: String, // asset symbol, for example `ETH`, `DAI`, `USDC`
    val name: String,
    val decimals: Long, // token decimals, e.g. 18 for ETH/DAI, 6 for USDC
    val type: String // asset type & network, e.g. `ETH`, `ERC20`, `MATIC_ERC20`
)