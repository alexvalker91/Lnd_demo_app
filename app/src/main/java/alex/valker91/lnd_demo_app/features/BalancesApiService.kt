package alex.valker91.lnd_demo_app.features

import retrofit2.http.GET
import retrofit2.http.Query

interface BalancesApiService {

    @GET("/api/balances")
    suspend fun getBalances(
        @Query("accountNumber") name: String
    ): List<BalanceApiResponse>
}