package alex.valker91.lnd_demo_app.features

data class MainScreenState(
    val accountNumber: String = "",
    val accountBalance: String = "",
    val accountId: String = "",
    val id: String = "",
    val isLoading: Boolean = false,
    val error: Exception? = null
)