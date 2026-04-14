package alex.valker91.lnd_demo_app.features

import javax.inject.Inject

class BalancesNetworkDataSource @Inject constructor(
    private val balancesApiService: BalancesApiService
) {

    suspend fun getListOfBalances(name: String): Result<List<BalanceApiResponse>> {
        return try {
            val list = balancesApiService.getBalances(name)
            Result.Success(list)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }
}