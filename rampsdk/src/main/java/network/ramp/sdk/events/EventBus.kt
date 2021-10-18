package network.ramp.sdk.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import network.ramp.sdk.events.model.Event

object EventBus {

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun invokeEvent(event: Event) = _events.emit(event)

}