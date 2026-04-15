package alex.valker91.lnd_demo_app.features

import android.util.Log
import java.util.UUID
import javax.inject.Inject

class CreateNewSynchronizedMoneyTransferUseCase @Inject constructor(
    private val balancesNetworkDataSource: BalancesNetworkDataSource
) {

    suspend fun execute(
        amount: Int,
        clientIdFrom: String,
        accountNumberFrom: String,
        accountNumberTo: String,
        comment: String
                        ): Result<SynchronizedMoneyTransferResponse> {
        Log.d("dsfsdfsdf", "$amount")
        Log.d("dsfsdfsdf", "$clientIdFrom")
        Log.d("dsfsdfsdf", "$accountNumberFrom")
        Log.d("dsfsdfsdf", "$accountNumberTo")
        val request  = SynchronizedMoneyTransferRequest(amount, clientIdFrom, accountNumberFrom, accountNumberTo, comment, UUID.randomUUID().toString())
        return balancesNetworkDataSource.createNewSynchronizedMoneyTransfer(request)
    }
}