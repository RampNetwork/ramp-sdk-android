package network.ramp.sdk.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


internal class UrlSafeCheckerTest {

    @Test
    fun `isUrlSafe should return true fore safe urls`() {
        val safeUrl1 = "https://ri-widget-dev2.firebaseapp.com"
        val safeUrl2 = "https://ri-widget-staging.firebaseapp.com"
        val safeUrl3 = "https://buy.ramp.network"
        val safeUrl4 = "https://ri-widget-dev-5.firebaseapp.com"
        val safeUrl5 = "https://ri-widget-dev-10.firebaseapp.com"

        Assertions.assertAll(
            { Assertions.assertTrue(UrlSafeChecker.isUrlSafe(safeUrl1)) },
            { Assertions.assertTrue(UrlSafeChecker.isUrlSafe(safeUrl2)) },
            { Assertions.assertTrue(UrlSafeChecker.isUrlSafe(safeUrl3)) },
            { Assertions.assertTrue(UrlSafeChecker.isUrlSafe(safeUrl4)) },
            { Assertions.assertTrue(UrlSafeChecker.isUrlSafe(safeUrl5)) }
        )
    }

    @Test
    fun `isUrlSafe should return false fore unsafe urls`() {

        val safeUrl1 = "https://ri-widget-devs2.firebaseapp.com"
        val safeUrl2 = "ri-widget-staging.firebaseapp.com"
        val safeUrl3 = "https://ngrok.io/buy.ramp.network"
        val safeUrl4 = "https://ri-widget-dev-5.firebaseapp.com/sadasd"
        val safeUrl5 = "https://ri-widget-dev.com/?https://ri-widget-devs2.firebaseapp.com"
        val safeUrl6 = "https://ri-widget-dev-s.firebaseapp.com"

        Assertions.assertAll(
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(safeUrl1)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(safeUrl2)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(safeUrl3)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(safeUrl4)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(safeUrl5)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(safeUrl6)) },
        )
    }
}