package com.example.graph_app.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.graph_app.core.utils.BaseFragment
import com.example.graph_app.core.utils.NumericKeyBoardTransformationMethod
import com.example.graph_app.core.utils.Resource
import com.example.graph_app.databinding.FragmentMainBinding
import com.example.graph_app.presentation.common.getDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMainBinding::inflate

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupComponents()
        observers()
        listeners()

    }

    private fun setupComponents() {
        with(binding) {
            editText.transformationMethod = NumericKeyBoardTransformationMethod()
            setButtonEnabledState(editText.text?.toString())
        }
    }

    private fun listeners() {
        with(binding) {
            editText.doOnTextChanged { text, _, _, _ ->
                setButtonEnabledState(text?.toString())
            }

            button.setOnClickListener {
                viewModel.getPoints(binding.editText.text.toString().toInt())
            }
        }
    }

    private fun observers() {
        with(viewModel) {

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    pointsResult.collect { result ->
                        binding.button.isVisible = result !is Resource.Loading
                        binding.progressBar.isVisible = result is Resource.Loading

                        when (result) {
                            is Resource.Success -> {
                                findNavController().navigate(
                                    MainFragmentDirections.actionMainFragmentToGraphFragment(
                                        result.data.toTypedArray()
                                    )
                                )
                                resetPointsResult()
                            }

                            is Resource.Error -> {
                                requireContext().getDialog(
                                    message = result.message,
                                    positiveButtonText = "Повторить",
                                    onPositiveButtonClick = {
                                        viewModel.getPoints(
                                            binding.editText.text.toString().toInt()
                                        )
                                    },
                                    negativeButtonText = "Отмена",
                                    onNegativeButtonClick = {
                                        viewModel.resetPointsResult()
                                    }
                                ).show()
                                resetPointsResult()
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setButtonEnabledState(text: String?) {
        binding.button.isEnabled = !text.isNullOrEmpty()
    }

}