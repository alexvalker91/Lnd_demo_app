package alex.valker91.lnd_demo_app.features

interface MainEvent

class GetBalance(val accountNumber: String) : MainEvent
class CreateNewSynchronizedMoneyTransfer(
    val amount: Int,
    val clientIdFrom: String,
    val accountNumberFrom: String,
    val accountNumberTo: String,
    val comment: String
) : MainEvent