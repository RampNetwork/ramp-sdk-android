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
        val safeUrl5 = "https://ri-widget-dev-42.firebaseapp.com"


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

        val unsafeUrl1 = "https://ri-widget-devs2.firebaseapp.com"
        val unsafeUrl2 = "ri-widget-staging.firebaseapp.com"
        val unsafeUrl3 = "https://ngrok.io/buy.ramp.network"
        val unsafeUrl4 = "https://ri-widget-dev-5.firebaseapp.com/sadasd"
        val unsafeUrl5 = "https://ri-widget-dev.com/?https://ri-widget-devs2.firebaseapp.com"
        val unsafeUrl6 = "https://ri-widget-dev-s.firebaseapp.com"
        val unsafeUrl7 = "https://ri-widget-dev-10.firebaseapp.comsd"
        val unsafeUrl8 = "https://ri-widget-dev-.firebaseapp.com"


        Assertions.assertAll(
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl1)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl2)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl3)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl4)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl5)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl6)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl7)) },
            { Assertions.assertFalse(UrlSafeChecker.isUrlSafe(unsafeUrl8)) }
        )
    }
}