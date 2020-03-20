package com.gmail.maystruks08.nfcruntracker.ui.register

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateFormat
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.eventbas.CardScannerLiveData
import kotlinx.android.synthetic.main.fragment_register_new_runner.*
import java.util.*
import javax.inject.Inject


class RegisterNewRunnerFragment : BaseFragment(R.layout.fragment_register_new_runner) {

    @Inject
    lateinit var viewModel: RegisterNewRunnerViewModel

    @Inject
    lateinit var cardScannerLiveData: CardScannerLiveData

    private val calendar = Calendar.getInstance()
    private var runnerSex: RunnerSex? = null
    private var runnerType: RunnerType? = null
    private var runnerDateOfBirthday: Date? = null
    private var runnerCardId: String? = null

    override fun injectDependencies() = App.registerNewRunnerComponent?.inject(this)

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.app_name)
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

        viewModel.onDateOfBirthdaySelected.observe(viewLifecycleOwner, Observer {
            runnerDateOfBirthday = it
            tvDateOfBirthday.text = it.toDateFormat()
        })

        cardScannerLiveData.onNfcReaderScannedCard.observe(viewLifecycleOwner, Observer {
            tvScanCard.text = "Карта: $it"
            runnerCardId = it
            viewModel.onNfcCardScanned(it)
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
            } else {
                context?.toast("Заполните обязательные поля!")
            }
        }
    }

    override fun onDestroyView() {
        App.clearRegisterNewRunnerComponent()
        super.onDestroyView()
    }

}
