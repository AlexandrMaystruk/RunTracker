package com.gmail.maystruks08.nfcruntracker.ui.settings

import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    lateinit var viewModel: SettingsViewModel

    override fun injectDependencies() {
        App.settingsComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_settings)
        .build()

    override fun bindViewModel() {
        viewModel.config.observe(viewLifecycleOwner, Observer {
            spinnerForRunners.setSelection(it.checkpointId?:0)
            spinnerForIronPeople.setSelection(it.checkpointIronPeopleId?:0)
            tvDateOfStart.text = "Дата старта: ${it.startDate?.toDateTimeFormat()}"
        })

        viewModel.start.observe(viewLifecycleOwner, Observer {
            tvDateOfStart.text = "Дата старта: ${it?.toDateTimeFormat()}"
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            context?.toast(it)
        })
    }

    override fun initViews() {
        viewModel.onInitViewsStarted()
        spinnerForRunners.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.onCurrentCheckpointChangedForRunners(position)
            }
        }

        spinnerForIronPeople.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.onCurrentCheckpointChangedForIronPeoples(position)
            }
        }

        tvStartRunning.setOnClickListener {
            viewModel.onCompetitionStart(Date())
        }

        tvSignOut.setOnClickListener {
            viewModel.onSignOutClicked()
        }
    }

    override fun onDestroyView() {
        App.clearSettingsComponent()
        super.onDestroyView()
    }
}
