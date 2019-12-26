package com.gmail.maystruks08.nfcruntracker.core.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    var toolbarManager: ToolbarManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarManager =  ToolbarManager(builder(), view).apply { prepareToolbar() }
        initViews()
    }

    protected abstract fun builder(): FragmentToolbar

    protected abstract fun initViews()

    override fun onDestroyView() {
        toolbarManager = null
        super.onDestroyView()
    }
}