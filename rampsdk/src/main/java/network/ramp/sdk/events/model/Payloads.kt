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
    val endTime: String,
    val escrowAddress: String? = null,
    val cryptoAmount: String,
    val fiatCurrency: String,
    val fiatValue: Long,
    val assetExchangeRateEur: Double,
    val fiatExchangeRateEur: Long,
    val baseRampFee: Double,
    val networkFee: Double,
    val appliedFee: Double,
    val createdAt: String,
    val updatedAt: String,
    val id: String,
    val asset: Asset,
    val receiverAddress: String,
    val assetExchangeRate: Double,
    val purchaseViewToken: String,
    val actions: List<Action>,
    val status: String,
    val paymentMethodType: String
)

data class Action(
    val newStatus: String,
    val timestamp: String
)

data class Asset(
    val address: String? = null,
    val symbol: String,
    val name: String,
    val decimals: Long,
    val type: String
)