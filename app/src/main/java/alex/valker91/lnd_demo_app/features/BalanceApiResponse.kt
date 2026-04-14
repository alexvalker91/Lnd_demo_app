package alex.valker91.lnd_demo_app.features

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BalanceApiResponse(
    @Json(name = "accountId") val accountId: Int,
    @Json(name = "accountBalance") val accountBalance: Double,
    @Json(name = "id") val id: Int
)