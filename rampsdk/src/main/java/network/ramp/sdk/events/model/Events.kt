package network.ramp.sdk.events.model


sealed class Event(val type: EventType)

data class WidgetClose(var payload: WidgetClosePayload? = null) : Event(EventType.WIDGET_CLOSE)

data class PurchasedFailed(var payload: String?) : Event(EventType.PURCHASE_FAILED)

data class PurchasedCreated(var payload: PurchaseCreatedPayload) : Event(EventType.PURCHASE_CREATED)

internal data class OpenLink(var payload: OpenLinkPayload) : Event(EventType.OPEN_LINK)

internal data class WidgetConfigDone(var payload: String?) : Event(EventType.WIDGET_CONFIG_DONE)

internal data class BackButtonPressed(var payload: String?): Event(EventType.BACK_BUTTON_PRESSED)

internal data class KycInit(var payload: KycInitPayload) : Event(EventType.KYC_INIT)

internal data class KycStarted(var payload: KycStartedPayload) : Event(EventType.KYC_STARTED)

internal data class KycFinished(var payload: KycFinishedPayload) : Event(EventType.KYC_FINISHED)

internal data class KycAborted(var payload: KycAbortedPayload) : Event(EventType.KYC_ABORTED)

internal data class KycSubmitted(var payload: KycSubmittedPayload) : Event(EventType.KYC_SUBMITTED)

internal data class KycError(var payload: KycErrorPayload) : Event(EventType.KYC_ERROR)


enum class EventType {
    WIDGET_CLOSE,
    OPEN_LINK,
    KYC_INIT,
    WIDGET_CONFIG_DONE,
    BACK_BUTTON_PRESSED,
    PURCHASE_FAILED,
    PURCHASE_CREATED,
    KYC_STARTED,
    KYC_FINISHED,
    KYC_ABORTED,
    KYC_SUBMITTED,
    KYC_ERROR
}

