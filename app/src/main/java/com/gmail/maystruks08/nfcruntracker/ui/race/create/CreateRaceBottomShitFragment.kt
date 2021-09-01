package com.gmail.maystruks08.nfcruntracker.ui.race.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.core.base.BaseBottomSheetDialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.setVisibility
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentCreateRaceBinding
import com.gmail.maystruks08.nfcruntracker.ui.race.RaceFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
@ObsoleteCoroutinesApi
class CreateRaceBottomShitFragment : BaseBottomSheetDialogFragment() {

    private val viewModel: CreateRaceViewModel by viewModels()

    private lateinit var binding: FragmentCreateRaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCreateRaceBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun bindViewModel() {
        with(viewModel) {
            validateInputState.observe(viewLifecycleOwner, {
                when (it) {
                    CorrectName -> binding.raceNameInputLayout.error = ""
                    NameInvalid -> binding.raceNameInputLayout.error = "Invalid name"
                    CorrectStartDate -> Unit
                    StartDateInvalid -> Unit
                }
            })
            showProgress.observe(viewLifecycleOwner, {
                binding.progressBar.setVisibility(it)
            })
            dismissDialog.observe(viewLifecycleOwner, {
                (parentFragmentManager.findFragmentByTag(RaceFragment.CREATE_RACE_DIALOG) as? CreateRaceBottomShitFragment)?.dismiss()
            })
        }
    }

    override fun initViews() {
        with(binding) {
            etRaceName.addTextChangedListener(afterTextChanged = {
                viewModel.onRaceNameChanged(it?.toString())
            })

            val today = Calendar.getInstance()
            raceStartDatePicker.minDate = today.time.time
            raceStartDatePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ) { _, year, monthOfYear, dayOfMonth ->
                today.set(year, monthOfYear, dayOfMonth)
                viewModel.onRaceDateChanged(today.time)
            }

            btnSaveNewRace.setOnClickListener {
                viewModel.onCreateRaceClicked()
            }
        }
    }

    companion object {
        fun getInstance() = CreateRaceBottomShitFragment()
    }
}
