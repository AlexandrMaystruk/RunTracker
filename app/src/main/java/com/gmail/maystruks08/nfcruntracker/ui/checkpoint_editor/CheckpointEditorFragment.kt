package com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor

import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel

class CheckpointEditorFragment : BaseFragment(R.layout.fragment_checkpoints) {

    lateinit var viewModel: CheckpointEditorFragment

    override fun injectDependencies() {
        App.checkpointEditorComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {

    }

    override fun initViews() {

    }

    override fun onDestroyView() {
        App.clearCheckpointEditorComponent()
        super.onDestroyView()
    }

    override fun clearInjectedComponents() = App.clearCheckpointEditorComponent()

    companion object {

        fun getInstance() = CheckpointEditorFragment()

    }
}
