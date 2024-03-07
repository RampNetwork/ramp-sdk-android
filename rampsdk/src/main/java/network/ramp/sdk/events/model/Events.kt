package network.ramp.sdk.events.model

import com.squareup.moshi.JsonClass

sealed class Event(val type: EventType) {
    companion object {
        const val SEND_CRYPTO_EVENT_VERSION = 1
        const val SEND_CRYPTO_RESULT_EVENT_VERSION = 1
    }
}

@JsonClass(generateAdapter = true)
data class Close(var payload: WidgetClosePayload? = null) : Event(EventType.CLOSE) // TO_VERIFY Not in documentation but in received events
@JsonClass(generateAdapter = true)
data class WidgetClose(var payload: WidgetClosePayload? = null) : Event(EventType.WIDGET_CLOSE)

@JsonClass(generateAdapter = true)
data class PurchasedFailed(var payload: String?) : Event(EventType.PURCHASE_FAILED)

@JsonClass(generateAdapter = true)
data class PurchasedCreated(var payload: PurchaseCreatedPayload) : Event(EventType.PURCHASE_CREATED)

@JsonClass(generateAdapter = true)
internal data class OpenLink(var payload: OpenLinkPayload) : Event(EventType.OPEN_LINK)

@JsonClass(generateAdapter = true)
internal data class WidgetConfigDone(var payload: String?) : Event(EventType.WIDGET_CONFIG_DONE)

@JsonClass(generateAdapter = true)
internal data class WidgetConfigFailed(var payload: String?) : Event(EventType.WIDGET_CONFIG_FAILED)

@JsonClass(generateAdapter = true)
internal data class BackButtonPressed(var payload: String?) : Event(EventType.BACK_BUTTON_PRESSED)

@JsonClass(generateAdapter = true)
internal data class SendCrypto(
    var payload: SendCryptoPayload,
    var eventVersion: Int = SEND_CRYPTO_EVENT_VERSION
) : Event(EventType.SEND_CRYPTO)

@JsonClass(generateAdapter = true)
internal data class SendCryptoResult(
    var payload: SendCryptoResultPayload,
    var eventVersion: Int = SEND_CRYPTO_RESULT_EVENT_VERSION
) : Event(EventType.SEND_CRYPTO_RESULT)

@JsonClass(generateAdapter = true)
internal data class OfframpSaleCreated(var payload: OfframpSaleCreatedPayload) :
    Event(EventType.OFFRAMP_SALE_CREATED)

@JsonClass(generateAdapter = false)
enum class EventType {
    CLOSE,
    WIDGET_CLOSE,
    OPEN_LINK,
    WIDGET_CONFIG_DONE,
    WIDGET_CONFIG_FAILED,
    BACK_BUTTON_PRESSED,
    PURCHASE_FAILED,
    PURCHASE_CREATED,
    SEND_CRYPTO,
    SEND_CRYPTO_RESULT,
    OFFRAMP_SALE_CREATED
}

