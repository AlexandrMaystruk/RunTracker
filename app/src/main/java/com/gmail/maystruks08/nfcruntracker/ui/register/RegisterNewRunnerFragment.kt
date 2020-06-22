package com.gmail.maystruks08.nfcruntracker.ui.register

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import kotlinx.android.synthetic.main.fragment_register_new_runner.*
import java.util.*


class RegisterNewRunnerFragment : BaseFragment(R.layout.fragment_register_new_runner) {

    lateinit var viewModel: RegisterNewRunnerViewModel

    private val calendar = Calendar.getInstance()
    private var runnerSex: RunnerSex? = null
    private var runnerType: RunnerType? = null
    private var runnerDateOfBirthday: Date? = null
    private var runnerCardId: String? = null

    override fun injectDependencies() {
        App.registerNewRunnerComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_register_new_runner)
        .build()

    override fun bindViewModel() {
        viewModel.selectDateOfBirthdayClicked.observe(viewLifecycleOwner, Observer {
            DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    viewModel.onDateOfBirthdaySelected(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        })

        viewModel.selectedDateOfBirthday.observe(viewLifecycleOwner, Observer {
            runnerDateOfBirthday = it
            tvDateOfBirthday.text = it.toDateFormat()
        })

        viewModel.scannedCard.observe(viewLifecycleOwner, Observer {
            tvScanCard.text = getString(R.string.card, it)
            runnerCardId = it
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            context?.toast(it)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            when(it){
                is SaveRunnerDataException -> context?.toast(getString(R.string.error_save_data_to_local_db))
                is SyncWithServerException -> context?.toast(getString(R.string.error_sync_with_server))
                is RunnerWithIdAlreadyExistException ->  context?.toast(getString(R.string.error_member_already_exist))
            }
        })
    }

    override fun initViews() {
        tvDateOfBirthday.setOnClickListener {
            viewModel.onSelectDateOfBirthdayClicked()
        }

        radioGroupSex.setOnCheckedChangeListener { _, checkedId ->
            runnerSex = when (checkedId) {
                R.id.rbMale -> RunnerSex.MALE
                R.id.rbFemale -> RunnerSex.FEMALE
                else -> null
            }
        }

        radioGroupRunnerType.setOnCheckedChangeListener { _, checkedId ->
            runnerType = when (checkedId) {
                R.id.rbRunner -> RunnerType.NORMAL
                R.id.rbIronRunner -> RunnerType.IRON
                else -> null
            }
        }

        btnAddNewRunner.setOnClickListener {
            if (etRunnerFullName.text.isNotEmpty() &&
                runnerSex != null &&
                runnerDateOfBirthday != null &&
                !etRunnerNumber.text.isNullOrEmpty() &&
                runnerType != null &&
                runnerCardId != null
            ) {
                viewModel.onRegisterNewRunner(
                    etRunnerFullName.text.toString(),
                    runnerSex!!,
                    runnerDateOfBirthday!!,
                    etRunnerCity.text.toString(),
                    etRunnerNumber.text.toString().toInt(),
                    runnerType!!,
                    runnerCardId!!
                )
            } else context?.toast(getString(R.string.fill_in_required_fields))
        }
    }

    override fun onDestroyView() {
        App.clearRegisterNewRunnerComponent()
        super.onDestroyView()
    }

}
