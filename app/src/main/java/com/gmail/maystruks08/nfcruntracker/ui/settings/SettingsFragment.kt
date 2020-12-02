package com.gmail.maystruks08.nfcruntracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.gmail.maystruks08.domain.toDateTimeFormat
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun injectDependencies() {
        App.settingsComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(inflater, container, false)
        .let { runnersBinding ->
            binding = runnersBinding
            return@let runnersBinding.root
        }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_settings)
        .build()

    override fun bindViewModel() {
        with(binding) {
            viewModel.config.observe(viewLifecycleOwner, {
                val adapterRunner =
                    ArrayAdapter(requireContext(), R.layout.spinner_item, it.checkpointsName)
                val adapterIron =
                    ArrayAdapter(requireContext(), R.layout.spinner_item, it.ironCheckpointsName)
                adapterRunner.setDropDownViewResource(R.layout.spinner_drop_down)
                adapterIron.setDropDownViewResource(R.layout.spinner_drop_down)

                spinnerForRunners.adapter = adapterRunner
                spinnerForIronPeople.adapter = adapterIron
                spinnerForRunners.setSelection(it.settings.checkpointId ?: 0)
                spinnerForIronPeople.setSelection(it.settings.checkpointIronPeopleId ?: 0)
                tvDateOfStart.text = getString(
                    R.string.date_of_start,
                    it.settings.startDate?.toDateTimeFormat() ?: ""
                )
            })

            viewModel.start.observe(viewLifecycleOwner, {
                tvDateOfStart.text = getString(R.string.date_of_start, it?.toDateTimeFormat() ?: "")
            })

            viewModel.changeStartButtonVisibility.observe(viewLifecycleOwner, {
                tvStartRunning.visibility = if (it) View.VISIBLE else View.GONE
            })

            viewModel.toast.observe(viewLifecycleOwner, {
                context?.toast(it)
            })
        }
    }

    override fun initViews() {
        with(binding) {
            tvDateOfStart.text = getString(R.string.date_of_start, "")
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
    }

    override fun clearInjectedComponents() = App.clearSettingsComponent()
}
