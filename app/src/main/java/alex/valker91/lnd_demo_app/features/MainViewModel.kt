package alex.valker91.lnd_demo_app.features

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
class MainViewModel @Inject constructor(
    private val getBalancesUseCase: GetBalancesUseCase,
    private val createNewSynchronizedMoneyTransferUseCase: CreateNewSynchronizedMoneyTransferUseCase
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<MainScreenState> =
        MutableStateFlow(MainScreenState(isLoading = false))

    val stateFlow: Flow<MainScreenState>
        get() = _stateFlow

    fun handleIntent(event: MainEvent) {
        when (event) {
            is GetBalance -> getBalance(event.accountNumber)
            is CreateNewSynchronizedMoneyTransfer -> createNewSynchronizedMoneyTransfer(event.amount,
                event.clientIdFrom,
                event.accountNumberFrom,
                event.accountNumberTo,
                event.comment)
        }
    }

//    private val _effect = MutableSharedFlow<CreateClientEffect>()
//    val effect: SharedFlow<CreateClientEffect> = _effect.asSharedFlow()

    private fun createNewSynchronizedMoneyTransfer(amount: Int,
                                                   clientIdFrom: String,
                                                   accountNumberFrom: String,
                                                   accountNumberTo: String,
                                                   comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _stateFlow.value =
                _stateFlow.value.copy(isLoading = true)
            delay(1_000)
            val result: Result<SynchronizedMoneyTransferResponse> =
                createNewSynchronizedMoneyTransferUseCase.execute(
                    amount,
                    clientIdFrom,
                    accountNumberFrom,
                    accountNumberTo,
                    comment
                )
            Log.d("dfasffsfasdf", "$result")
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(
                                originatorId = result.data.originatorId,
                                isLoading = false,
                                error = null)
                    }

                    is Result.Error -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(
                                isLoading = false,
                                error = result.error
                            )
                    }

                    is Result.Loading -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(isLoading = true)
                    }
                }
            }
        }
        }

    private fun getBalance(accountNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _stateFlow.value =
                _stateFlow.value.copy(isLoading = true)
            delay(1_000)
            val result: Result<BalanceApiResponse> = getBalancesUseCase.execute(accountNumber)
            Log.d("dfasffsfasdf","$result")
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(
                                accountBalance = result.data.accountBalance.toString(),
                                accountId = result.data.accountId.toString(),
                                id = result.data.id.toString(),
                                isLoading = false,
                                error = null)
//                        _effect.emit(CreateClientEffect.NavigateBackWithSuccess)
                    }
                    is Result.Error -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(
                                accountBalance = "",
                                accountId = "",
                                id = "",
                                isLoading = false,
                                error = result.error)
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