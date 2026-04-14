package alex.valker91.lnd_demo_app.features

import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val balancesNetworkDataSource: BalancesNetworkDataSource
) {

    suspend fun execute(name: String): Result<BalanceApiResponse> {
        return when (val result = balancesNetworkDataSource.getListOfBalances(name)) {
            is Result.Success -> {
                val firstBalance = result.data.firstOrNull()
                if (firstBalance != null) {
                    Result.Success(firstBalance)
                } else {
                    Result.Error(NoSuchElementException("Список балансов пуст"))
                }
            }

            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}