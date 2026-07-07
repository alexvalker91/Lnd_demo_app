package alex.valker91.lnd_demo_app.second

data class SecondScreenState (
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: Exception? = null
)