package com.gmail.maystruks08.nfcruntracker.ui.register

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.exception.EmptyRegistrationRunnerDataException
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import kotlinx.android.synthetic.main.fragment_register_new_runner.*


class RegisterNewRunnerFragment : BaseFragment(R.layout.fragment_register_new_runner) {

    lateinit var viewModel: RegisterNewRunnerViewModel

    lateinit var adapter: RegisterRunnerAdapter

    private var teamName: String? = null

    override fun injectDependencies() {
        App.registerNewRunnerComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_register_new_runner)
        .build()

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
        adapter = RegisterRunnerAdapter {
            viewModel.onCreateTeamMemberClick()
        }
        registerRunnersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RegisterNewRunnerFragment.adapter
        }

        btnAddNewRunner.setOnClickListener {
            viewModel.onRegisterNewRunnerClicked(adapter.runnerRegisterData, teamName)
        }

        etRunnerTeamName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { teamName = s?.toString() }
            override fun afterTextChanged(s: Editable?) {}
        })
        initStaticCardSwipe()
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
        ItemTouchHelper(swipeHelper).attachToRecyclerView(registerRunnersRecyclerView)
    }

    //This is bad solution, move logic to view model, need to refactor
    private fun resolveTeamNameVisibility() {
        inputLayout.visibility = if (adapter.runnerRegisterData.size > 1) View.VISIBLE else {
            teamName = null
            View.GONE
        }
    }

    override fun onDestroyView() {
        App.clearRegisterNewRunnerComponent()
        super.onDestroyView()
    }

}
