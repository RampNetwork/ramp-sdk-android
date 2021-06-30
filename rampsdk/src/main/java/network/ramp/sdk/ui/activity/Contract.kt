package network.ramp.sdk.ui.activity

import network.ramp.sdk.facade.Config

internal interface Contract {
    interface View {

        fun showDialog()

        fun close()

        fun sendPostMessage(data: String)

    }

    interface Presenter {

        fun handlePostMessage(json: String)

        fun buildUrl(config: Config): String
    }
}
