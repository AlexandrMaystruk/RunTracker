package com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentCheckpointsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckpointEditorFragment : BaseFragment() {

    private val viewModel: CheckpointEditorViewModel by viewModels()
    private lateinit var binding: FragmentCheckpointsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCheckpointsBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {}

    override fun initViews() {}

    companion object {

        fun getInstance() = CheckpointEditorFragment()

    }
}
