package com.gmail.maystruks08.nfcruntracker.ui.register

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateFormat
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
            tvDateOfBirthday.text = it.toDateFormat()
        })

        cardScannerLiveData.onNfcReaderScannedCard.observe(viewLifecycleOwner, Observer {
            tvScanCard.text = "Id карта: $it"
            viewModel.onNfcCardScanned(it)
        })
    }


    override fun initViews() {
        tvDateOfBirthday.setOnClickListener {
            viewModel.onSelectDateOfBirthdayClicked()
        }
    }

    override fun onDestroyView() {
        App.clearRegisterNewRunnerComponent()
        super.onDestroyView()
    }

}
