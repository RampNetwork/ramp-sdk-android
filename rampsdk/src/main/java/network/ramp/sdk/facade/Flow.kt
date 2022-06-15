package network.ramp.sdk.facade

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Flow : Parcelable {
    OFFRAMP, ONRAMP
}