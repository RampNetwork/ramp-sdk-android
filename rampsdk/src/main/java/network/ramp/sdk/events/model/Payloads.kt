package network.ramp.sdk.events.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

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
internal data class SendCryptoPayload(
    var assetInfo: Asset,
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
data class OfframpSaleCreatedPayload(
    val sale: OfframpSale,
    val saleViewToken: String,
    val apiUrl: String
)


@JsonClass(generateAdapter = true)
data class Purchase(
    val id: String,
    val endTime: String, // purchase validity time, ISO date-time string
    val cryptoAmount: String, // number-string, in wei or token units
    val fiatCurrency: String, // three-letter currency code
    val fiatValue: Double, // total value the user pays for the purchase, in fiatCurrency
    val baseRampFee: Double, // base Ramp fee before any modifications, in fiatCurrency
    val networkFee: Double, // network fee for transferring the purchased asset, in fiatCurrency
    val appliedFee: Double, // final fee the user pays (included in fiatValue), in fiatCurrency
    val createdAt: String, // ISO date-time string
    val updatedAt: String, // ISO date-time string
    val asset: Asset, // description of the purchased asset (address, symbol, name, decimals)
    val receiverAddress: String, // blockchain address of the buyer
    val assetExchangeRate: Double,// price of 1 whole token of purchased asset, in fiatCurrency
    val status: String, // purchase status
    val paymentMethodType: String, // type of payment method used to pay for the swap: 'MANUAL_BANK_TRANSFER' | 'AUTO_BANK_TRANSFER' | 'CARD_PAYMENT' | 'APPLE_PAY'
    val finalTxHash: String? = null // TO_VERIFY() not in recived json
)

@JsonClass(generateAdapter = true)
data class OfframpSale(
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
@Parcelize
data class Asset(
    val address: String? = null, // 0x-prefixed address for ERC-20 tokens, `null` for ETH
    val symbol: String, // asset symbol, for example `ETH`, `DAI`, `USDC`
    val apiV3Symbol: String, // TO VERIFY new
    val name: String,
    val decimals: Long, // token decimals, e.g. 18 for ETH/DAI, 6 for USDC
    val type: String, // asset type & network, e.g. `ETH`, `ERC20`, `MATIC_ERC20`
    val chain: String, // asset chain, for example `ETH`, `BSC`, `POLKADOT`
    val apiV3Type: String // TO_VERIFY() new
) : Parcelable