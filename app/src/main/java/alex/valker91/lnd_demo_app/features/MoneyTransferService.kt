package alex.valker91.lnd_demo_app.features

import retrofit2.http.Body
import retrofit2.http.GET

interface MoneyTransferService {

    @GET("/api/payments")
    suspend fun createNewSynchronizedMoneyTransfer(
        @Body request: SynchronizedMoneyTransferRequest
    ): SynchronizedMoneyTransferResponse
}