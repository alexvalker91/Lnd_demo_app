package alex.valker91.lnd_demo_app.features

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SynchronizedMoneyTransferResponse (
    @Json(name = "amount") val amount: Int,
    @Json(name = "clientIdFrom") val clientIdFrom: String,
    @Json(name = "accountNumberFrom") val accountNumberFrom: String,
    @Json(name = "accountNumberTo") val accountNumberTo: String,
    @Json(name = "comment") val comment: String,
    @Json(name = "originatorId") val originatorId: String,
    @Json(name = "id") val id: Int,
    @Json(name = "timestamp") val timestamp: String
)