package alex.valker91.lnd_demo_app.features

interface MainEvent

class GetBalance(val accountNumber: String) : MainEvent