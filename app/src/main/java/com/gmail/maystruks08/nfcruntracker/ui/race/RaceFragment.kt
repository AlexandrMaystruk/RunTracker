package com.gmail.maystruks08.nfcruntracker.ui.race

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRaceBinding
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.SwipeActionHelper
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RaceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@AndroidEntryPoint
class RaceFragment : BaseFragment(), RaceAdapter.Interaction {

    private val viewModel: RaceViewModel by viewModels()

    private lateinit var binding: FragmentRaceBinding
    private lateinit var adapter: RaceAdapter

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withMenu(R.menu.menu_search)
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) { viewModel.onSearchQueryChanged(it) }
        .withTitle(R.string.screen_race_list)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRaceBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun bindViewModel() {
        viewModel.races.observe(viewLifecycleOwner, {
            adapter.raceList = it
        })

        viewModel.showProgress.observe(viewLifecycleOwner, {
            //TODO fix progress
        })
    }

    override fun initViews() {
        adapter = RaceAdapter(this)
        with(binding) {
            raceRecyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = this@RaceFragment.adapter
            }

            btnCreateNewRace.setOnClickListener {
                viewModel.onCreateNewRaceClicked()
            }
        }
        initStaticCardSwipe()

        viewModel.initUI()
    }

    override fun onClickAtRace(raceView: RaceView) {
        viewModel.onRaceClicked(raceView)
    }

    private fun initStaticCardSwipe() {
        val swipeHelper = object : SwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {


                }
            }
        }
        ItemTouchHelper(swipeHelper).attachToRecyclerView(binding.raceRecyclerView)
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard()
    }

    override fun onDestroyView() {
        binding.raceRecyclerView.adapter = null
        super.onDestroyView()
    }

    companion object{

        fun getInstance() =  RaceFragment()

    }

}
