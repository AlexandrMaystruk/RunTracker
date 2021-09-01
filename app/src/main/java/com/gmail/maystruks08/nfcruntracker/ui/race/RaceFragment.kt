package com.gmail.maystruks08.nfcruntracker.ui.race

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.findFragmentByTag
import com.gmail.maystruks08.nfcruntracker.core.ext.setVisibility
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRaceBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.race.create.CreateRaceBottomShitFragment
import com.gmail.maystruks08.nfcruntracker.ui.view_models.RaceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@AndroidEntryPoint
class RaceFragment : BaseFragment(R.layout.fragment_race), RaceViewHolderManager.Interaction {

    private val viewModel: RaceViewModel by viewModels()
    private val binding: FragmentRaceBinding by viewBinding {
        raceRecyclerView.adapter = null
    }
    private lateinit var adapter: AppAdapter

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withMenu(R.menu.menu_search)
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) { viewModel.onSearchQueryChanged(it) }
        .withTitle(R.string.screen_race_list)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AppAdapter(listOf(RaceViewHolderManager(this)))
    }

    override fun bindViewModel() {
        viewModel.races.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        viewModel.showCreateRaceDialog.observe(viewLifecycleOwner, { needToShow ->
            if (needToShow) {
                CreateRaceBottomShitFragment
                    .getInstance()
                    .show(childFragmentManager, CREATE_RACE_DIALOG)
            } else {
                findFragmentByTag<CreateRaceBottomShitFragment>(CREATE_RACE_DIALOG)?.dismiss()
            }
        })

        viewModel.showProgress.observe(viewLifecycleOwner, {
            binding.progress.setVisibility(it)
        })
    }

    override fun initViews() {
        with(binding) {
            raceRecyclerView.adapter = this@RaceFragment.adapter

            btnCreateNewRace.setOnClickListener {
                viewModel.onCreateNewRaceClicked()
            }
        }
        viewModel.initUI()
    }

    override fun onItemSelected(item: RaceView) {
        viewModel.onRaceClicked(item)
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard()
    }

    companion object {

        const val CREATE_RACE_DIALOG = "CREATE_RACE_DIALOG"

        fun getInstance() = RaceFragment()

    }

}
