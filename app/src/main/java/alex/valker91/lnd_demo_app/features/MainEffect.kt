package alex.valker91.lnd_demo_app.features

sealed class MainEffect {
    object ShowSuccessToast : MainEffect()
    data class ShowErrorToast(val message: String) : MainEffect()
}