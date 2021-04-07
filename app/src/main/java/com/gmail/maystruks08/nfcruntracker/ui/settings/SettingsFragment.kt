package com.gmail.maystruks08.nfcruntracker.ui.settings

import android.view.View
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
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
            tvSignOut.setOnClickListener {
                viewModel.onSignOutClicked()
            }
        }
    }
}
