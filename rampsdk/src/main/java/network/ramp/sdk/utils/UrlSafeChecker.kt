package network.ramp.sdk.utils

object UrlSafeChecker {

    private val listOfSafeUrls = listOf(
        "https://ri-widget-dev2.firebaseapp.com",
        "https://ri-widget-staging.firebaseapp.com",
        "https://buy.ramp.network",
        "https://app.dev.ramp-network.org",
        "https://app.demo.ramp.network",
        "https://app.ramp.network"
    )
    private val listOfSafeRegex = listOf("^https://ri-widget-dev-(\\d+)\\.firebaseapp\\.com$", "^https://app.(\\d+)\\.dev\\.ramp-network\\.org")

    fun isUrlSafe(url: String) = checkStaticUrls(url) || checkRegexList(url)

    private fun checkStaticUrls(url: String): Boolean = listOfSafeUrls.contains(url)

    private fun checkRegexList(url: String): Boolean {
        var isMatch = false
        listOfSafeRegex.forEach {
            val regex = Regex(pattern = it, options = setOf(RegexOption.IGNORE_CASE))
            if (regex.matches(url))
                isMatch = true
        }
        return isMatch
    }
}