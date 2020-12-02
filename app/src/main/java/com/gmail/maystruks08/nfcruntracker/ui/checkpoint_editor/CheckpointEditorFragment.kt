package com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentCheckpointsBinding

class CheckpointEditorFragment : BaseFragment() {

    private lateinit var binding: FragmentCheckpointsBinding
    private lateinit var viewModel: CheckpointEditorFragment

    override fun injectDependencies() {
        App.checkpointEditorComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCheckpointsBinding.inflate(inflater, container, false)
            .let { checkpointsBinding ->
                binding = checkpointsBinding
                return@let binding.root
            }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {}

    override fun initViews() {}

    override fun onDestroyView() {
        App.clearCheckpointEditorComponent()
        super.onDestroyView()
    }

    override fun clearInjectedComponents() = App.clearCheckpointEditorComponent()

    companion object {

        fun getInstance() = CheckpointEditorFragment()

    }
}
