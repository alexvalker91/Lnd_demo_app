package alex.valker91.lnd_demo_app.features

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alex.valker91.lnd_demo_app.databinding.FragmentMainBinding
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import io.sentry.Sentry

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerFlow()
        observerButton()
        observeEffects()
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is MainEffect.ShowSuccessToast -> {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        }
                        is MainEffect.ShowErrorToast -> {
                            Toast.makeText(requireContext(), effect.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun observerFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { result ->
                    binding.tvAccountBalance1.text = result.accountBalance
                    binding.tvAccountId1.text = result.accountId
                    binding.tvId1.text = result.id
                    binding.OriginatorId.text = result.originatorId
                    binding.spinner.root.isVisible = result.isLoading

                    if (!result.isLoading) {
                        Sentry.reportFullyDisplayed()
                    }
                }
            }
        }
    }

    private fun observerButton() {
        binding.btnGetBalances.setOnClickListener {
            Log.d("my_test_log", "click btn_get_balance_full_time ${System.currentTimeMillis()}")

            viewModel.handleIntent(GetBalance(binding.etAccountNumber.text.toString()))
        }

        binding.btnCreate.setOnClickListener {
            Log.d("MyPerfTest", "Запуск трейса btn_create_transfer_full_time")

            viewModel.handleIntent(
                CreateNewSynchronizedMoneyTransfer(
                    amount = binding.amount.text.toString().toIntOrNull() ?: 0,
                    binding.clientIdFrom.text.toString(),
                    binding.accountNumberFrom.text.toString(),
                    binding.accountNumberTo.text.toString(),
                    binding.comment.text.toString()
                )
            )
        }

        binding.fabAction.setOnClickListener {
            Log.d("my_test_log_fab_click", "click fab ${System.currentTimeMillis()}")
            val span = Sentry.getSpan()?.startChild("navigation", "Main -> Second")
                ?: Sentry.startTransaction("NavigateToSecond", "navigation")

            val action = MainFragmentDirections.actionMainFragmentToSecondFragment()
            findNavController().navigate(action)

            span.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}