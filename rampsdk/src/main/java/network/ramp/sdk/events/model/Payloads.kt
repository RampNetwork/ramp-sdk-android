package network.ramp.sdk.events.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class OpenLinkPayload(val linkType: String, val url: String)


@JsonClass(generateAdapter = true)
data class WidgetClosePayload(
    val showAlert: Boolean,
    val descriptionText: String? = null,
    val acceptText: String? = null,
    val rejectText: String? = null
)

@JsonClass(generateAdapter = true)
internal data class KycInitPayload(
    val email: String,
    val countryCode: String,
    val verificationId: Int,
    val metaData: String?,
    val apiKey: String,
    val provider: String
)

@JsonClass(generateAdapter = true)
internal data class KycStartedPayload(var verificationId: Int = 0)

@JsonClass(generateAdapter = true)
internal data class KycFinishedPayload(
    var verificationId: Int = 0,
    var identityAccessKey: String = ""
)

@JsonClass(generateAdapter = true)
internal data class KycAbortedPayload(var verificationId: Int = 0)

@JsonClass(generateAdapter = true)
internal data class KycSubmittedPayload(
    var verificationId: Int = 0,
    var identityAccessKey: String = ""
)

@JsonClass(generateAdapter = true)
internal data class KycErrorPayload(var verificationId: Int = 0)

@JsonClass(generateAdapter = true)
internal data class SendCryptoPayload(
    var assetInfo: Asset = "",
    var amount: String = "",
    var address: String = ""
)

@JsonClass(generateAdapter = true)
internal data class SendCryptoResultPayload(
    var txHash: String? = null,
    var error: String? = null
)

@JsonClass(generateAdapter = true)
data class PurchaseCreatedPayload(
    val purchase: Purchase,
    val purchaseViewToken: String,
    val apiUrl: String
)

@JsonClass(generateAdapter = true)
data class OffRampSaleCreatedPayload(
    val sale: OffRampSale,
    val saleViewToken: String,
    val apiUrl: String
)


@JsonClass(generateAdapter = true)
data class Purchase(
    val id: String,
    val endTime: String, // purchase validity time, ISO date-time string
    @Deprecated("This parameter will be removed in future release")
    val escrowAddress: String? = null, // filled only for escrow-backend purchases
    val cryptoAmount: String, // number-string, in wei or token units
    val fiatCurrency: String, // three-letter currency code
    val fiatValue: Long, // total value the user pays for the purchase, in fiatCurrency
    @Deprecated("This parameter will be removed in future")
    val assetExchangeRateEur: Double,
    @Deprecated("This parameter will be removed in future")
    val fiatExchangeRateEur: Long,
    val baseRampFee: Double, // base Ramp fee before any modifications, in fiatCurrency
    val networkFee: Double, // network fee for transferring the purchased asset, in fiatCurrency
    val appliedFee: Double, // final fee the user pays (included in fiatValue), in fiatCurrency
    val createdAt: String, // ISO date-time string
    val updatedAt: String, // ISO date-time string
    val asset: Asset, // description of the purchased asset (address, symbol, name, decimals)
    val receiverAddress: String, // blockchain address of the buyer
    val assetExchangeRate: Double,// price of 1 whole token of purchased asset, in fiatCurrency
    @Deprecated("This parameter will be removed in future")
    val purchaseViewToken: String,
    val status: String, // purchase status
    val paymentMethodType: String, // type of payment method used to pay for the swap: 'MANUAL_BANK_TRANSFER' | 'AUTO_BANK_TRANSFER' | 'CARD_PAYMENT' | 'APPLE_PAY'
    val finalTxHash: String? = null // hash of the crypto transfer blockchain transaction, filled once available
)

@JsonClass(generateAdapter = true)
data class OffRampSale(
    val id: String,
    val createdAt: String, // ISO date-time string
    val crypto: Crypto,
    val fiat: Fiat
)

@JsonClass(generateAdapter = true)
data class Crypto(
    val amount: String,
    val assetInfo: Asset, // description of the purchased asset (address, symbol, name, decimals, chain)
)

@JsonClass(generateAdapter = true)
data class Fiat(
    val amount: Double,
    val currencySymbol: String, // description of the purchased asset (address, symbol, name, decimals, chain)
)

@JsonClass(generateAdapter = true)
data class Asset(
    val address: String? = null, // 0x-prefixed address for ERC-20 tokens, `null` for ETH
    val symbol: String, // asset symbol, for example `ETH`, `DAI`, `USDC`
    val name: String,
    val decimals: Long, // token decimals, e.g. 18 for ETH/DAI, 6 for USDC
    val type: String // asset type & network, e.g. `ETH`, `ERC20`, `MATIC_ERC20`
    val chain: String // asset chain, for example `ETH`, `BSC`, `POLKADOT`
)