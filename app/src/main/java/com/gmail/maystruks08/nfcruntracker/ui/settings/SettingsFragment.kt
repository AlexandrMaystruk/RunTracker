package com.gmail.maystruks08.nfcruntracker.ui.settings

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.domain.toDateTimeFormat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.*

@ObsoleteCoroutinesApi
@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    private val binding: FragmentSettingsBinding by viewBinding()

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_settings)
        .build()

    override fun bindViewModel() {
        with(binding) {
            viewModel.config.observe(viewLifecycleOwner, {
                val adapterRunner = ArrayAdapter(requireContext(), R.layout.spinner_item, it.checkpointsName)
                val adapterIron = ArrayAdapter(requireContext(), R.layout.spinner_item, it.ironCheckpointsName)
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
}
