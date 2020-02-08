package com.gmail.maystruks08.nfcruntracker.core.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment(private val layout: Int? = null) : Fragment() {

    var toolbarManager: ToolbarManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layout?.let { inflater.inflate(layout, container, false) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        toolbarManager = ToolbarManager(initToolbar(), view).apply { prepareToolbar() }
        initViews()
    }

    protected abstract fun injectDependencies(): Unit?

    protected abstract fun initToolbar(): FragmentToolbar

    protected abstract fun bindViewModel()

    protected open fun initViews() {}

    override fun onDestroyView() {
        toolbarManager = null
        super.onDestroyView()
    }
}