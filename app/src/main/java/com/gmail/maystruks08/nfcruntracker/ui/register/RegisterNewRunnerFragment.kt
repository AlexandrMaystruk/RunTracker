package com.gmail.maystruks08.nfcruntracker.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.exception.EmptyRegistrationRunnerDataException
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRegisterNewRunnerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterNewRunnerFragment : BaseFragment() {

    private val viewModel: RegisterNewRunnerViewModel by viewModels()

    private lateinit var binding: FragmentRegisterNewRunnerBinding
    private lateinit var adapter: RegisterRunnerAdapter

    private var teamName: String? = null


    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_register_new_runner)
        .withMenu(R.menu.menu_save)
        .withMenuItems(
            listOf(R.id.action_save_changes),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onRegisterNewRunnerClicked(adapter.runnerRegisterData, teamName)
                true
            })
        )
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRegisterNewRunnerBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun bindViewModel() {
        viewModel.scannedCard.observe(viewLifecycleOwner, {
            adapter.setRunnerCardId(it)
        })

        viewModel.addNewTeamMemberItem.observe(viewLifecycleOwner, {
            adapter.addInputData(it)
            resolveTeamNameVisibility()
        })

        viewModel.toast.observe(viewLifecycleOwner, {
            context?.toast(it)
        })

        viewModel.error.observe(viewLifecycleOwner, {
            when (it) {
                is SaveRunnerDataException -> context?.toast(getString(R.string.error_save_data_to_local_db))
                is SyncWithServerException -> context?.toast(getString(R.string.error_sync_with_server))
                is RunnerWithIdAlreadyExistException -> context?.toast(getString(R.string.error_member_already_exist))
                is EmptyRegistrationRunnerDataException -> context?.toast(getString(R.string.fill_in_required_fields))
            }
        })
    }

    override fun initViews() {
        adapter = RegisterRunnerAdapter()
        with(binding){
            registerRunnersRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@RegisterNewRunnerFragment.adapter
            }
            btnAddTeamMemberRunner.setOnClickListener {
                viewModel.onCreateTeamMemberClick()
            }
            etRunnerTeamName.addTextChangedListener {
                teamName = it?.toString()
            }
        }
        initStaticCardSwipe()
        resolveTeamNameVisibility()
    }

    fun onNfcCardScanned(cardId: String){
        viewModel.onNfcCardScanned(cardId)
    }

    private fun initStaticCardSwipe() {
        val swipeHelper = object : SwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT && adapter.canSwipe()) {
                    adapter.removeInputField(position)
                    resolveTeamNameVisibility()
                }
            }
        }
        ItemTouchHelper(swipeHelper).attachToRecyclerView(binding.registerRunnersRecyclerView)
    }

    //This is bad solution, move logic to view model, need to refactor
    private fun resolveTeamNameVisibility() {
        binding.inputLayout.visibility = if (adapter.runnerRegisterData.size > 1) View.VISIBLE else {
            teamName = null
            View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard()
    }

}
