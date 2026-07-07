    package alex.valker91.lnd_demo_app.features

    import alex.valker91.lnd_demo_app.second.GetUserById
    import alex.valker91.lnd_demo_app.second.SecondEffect
    import alex.valker91.lnd_demo_app.second.SecondEvent
    import alex.valker91.lnd_demo_app.second.SecondScreenState
    import alex.valker91.lnd_demo_app.traceSuspend
    import android.os.Build
    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import dagger.hilt.android.lifecycle.HiltViewModel
    import io.sentry.Sentry
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.MutableSharedFlow
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.SharedFlow
    import kotlinx.coroutines.flow.asSharedFlow
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import javax.inject.Inject

    @HiltViewModel
    class SecondViewModel @Inject constructor(
        private val getUserByIdUseCase: GetUserByIdUseCase
    ) : ViewModel() {

        private val _stateFlow: MutableStateFlow<SecondScreenState> =
            MutableStateFlow(SecondScreenState(isLoading = false))

        val stateFlow: Flow<SecondScreenState>
            get() = _stateFlow

        fun handleIntent(event: SecondEvent) {
            when (event) {
                is GetUserById -> getUserById(event.id)
            }
        }

        private val _effect = MutableSharedFlow<SecondEffect>()
        val effect: SharedFlow<SecondEffect> = _effect.asSharedFlow()

        private fun getUserById(id: String) {
            viewModelScope.launch(Dispatchers.IO) {
                Sentry.setTag("device_model", Build.MODEL)
                Log.d("MyAutomation", "Current device_model = ${Build.MODEL}")
                _stateFlow.value =
                    _stateFlow.value.copy(isLoading = true)
                delay(2_000)

//                val result: Result<UserResponse> = traceSuspend("getUserByIdUseCase") {
//                    getUserByIdUseCase.execute(id.toLong())
//                }
                val result: Result<UserResponse> = traceSuspend("GetUserByIdTransaction") {
                    getUserByIdUseCase.execute(id.toLong())
                }

                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Success -> {
                            _stateFlow.value =
                                _stateFlow.value.copy(
                                    id = result.data.id.toString(),
                                    name = result.data.name.toString(),
                                    email = result.data.email.toString(),
                                    isLoading = false,
                                    error = null)
                            _effect.emit(SecondEffect.ShowSuccessToast("name=${result.data.name}, email=${result.data.email}"))
                        }
                        is Result.Error -> {
                            _stateFlow.value =
                                _stateFlow.value.copy(
                                    id = "",
                                    name = "",
                                    email = "",
                                    isLoading = false,
                                    error = result.error)
                            val errorMessage = result.error.message ?: "Unknown Error"
                            _effect.emit(SecondEffect.ShowErrorToast(errorMessage))
                        }
                        is Result.Loading -> {
                            _stateFlow.value =
                                _stateFlow.value.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }