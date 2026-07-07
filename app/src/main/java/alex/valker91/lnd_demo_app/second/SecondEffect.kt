package alex.valker91.lnd_demo_app.second

sealed class SecondEffect {

    data class ShowSuccessToast(val message: String) : SecondEffect()
    data class ShowErrorToast(val message: String) : SecondEffect()
}