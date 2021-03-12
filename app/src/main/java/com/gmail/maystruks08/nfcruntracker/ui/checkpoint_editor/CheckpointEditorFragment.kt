package com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor

import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentCheckpointsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckpointEditorFragment : BaseFragment(R.layout.fragment_checkpoints) {

    private val viewModel: CheckpointEditorViewModel by viewModels()
    private val binding: FragmentCheckpointsBinding by viewBinding {

    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {}

    override fun initViews() {}

    companion object {

        fun getInstance() = CheckpointEditorFragment()

    }
}
