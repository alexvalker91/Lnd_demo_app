package alex.valker91.lnd_demo_app.features

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alex.valker91.lnd_demo_app.databinding.FragmentMainBinding
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

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
    }

    private fun observerFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { result ->
                    binding.tvAccountBalance1.text = result.accountBalance
                    binding.tvAccountId1.text = result.accountId
                    binding.tvId1.text = result.id
                    binding.OriginatorId.text = result.originatorId
                    if (result.isLoading) {
                        binding.spinner.root.isVisible = true
                    } else {
                        binding.spinner.root.isVisible = false
                    }
                }
            }
        }
    }

    private fun observerButton() {
        binding.btnGetBalances.setOnClickListener {
            viewModel.handleIntent(GetBalance(binding.etAccountNumber.text.toString()))
        }

        binding.btnCreate.setOnClickListener {
            viewModel.handleIntent(CreateNewSynchronizedMoneyTransfer(
                binding.amount.text.toString().toInt(),
                binding.clientIdFrom.text.toString(),
            binding.accountNumberFrom.text.toString(),
            binding.accountNumberTo.text.toString(),
            binding.comment.text.toString())
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}