package alex.valker91.lnd_demo_app.features

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alex.valker91.lnd_demo_app.R
import alex.valker91.lnd_demo_app.databinding.FragmentSecondBinding
import alex.valker91.lnd_demo_app.second.GetUserById
import alex.valker91.lnd_demo_app.second.SecondEffect
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.Sentry
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecondViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("my_test_log_fab_click", "open fab ${System.currentTimeMillis()}")

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        observerFlow()
        observerButton()
        observeEffects()

        binding.fabGame.setOnClickListener {
            findNavController().navigate(R.id.action_secondFragment_to_gameFragment)
        }
    }

    private fun observerButton() {
        binding.button.setOnClickListener {
            Log.d("my_test_log", "click btn_get_balance_full_time ${System.currentTimeMillis()}")

            viewModel.handleIntent(GetUserById("2"))
        }
    }

    private fun observerFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { result ->

                    if (!result.isLoading) {
                        Sentry.reportFullyDisplayed()
                    }
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is SecondEffect.ShowSuccessToast -> {
                            Toast.makeText(requireContext(), "Success ${effect.message}", Toast.LENGTH_SHORT).show()
                        }
                        is SecondEffect.ShowErrorToast -> {
                            Toast.makeText(requireContext(), effect.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}