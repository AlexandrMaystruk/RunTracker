package com.gmail.maystruks08.nfcruntracker.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment(private val layout : Int) : Fragment() {

    var toolbarManager: ToolbarManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        toolbarManager =  ToolbarManager(initToolbar(), view).apply { prepareToolbar() }
        initViews()
    }

    protected abstract fun injectDependencies()

    protected abstract fun initToolbar(): FragmentToolbar

    protected abstract fun bindViewModel()

    protected abstract fun initViews()

    override fun onDestroyView() {
        toolbarManager = null
        super.onDestroyView()
    }
}