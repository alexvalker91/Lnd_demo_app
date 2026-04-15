package alex.valker91.lnd_demo_app.features

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MoneyTransferService {

    @POST("/api/payments")
    suspend fun createNewSynchronizedMoneyTransfer(
        @Body request: SynchronizedMoneyTransferRequest
    ): SynchronizedMoneyTransferResponse
}