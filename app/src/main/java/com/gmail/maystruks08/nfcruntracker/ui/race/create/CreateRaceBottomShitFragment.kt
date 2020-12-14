package com.gmail.maystruks08.nfcruntracker.ui.race.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentCreateRaceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRaceBottomShitFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateRaceBinding

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { }
        .withTitle(R.string.screen_race_list)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCreateRaceBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun bindViewModel() {


    }

    override fun initViews() {
        with(binding) {

        }

    }
}
