package com.gmail.maystruks08.nfcruntracker.core.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.DaggerViewModelFactory
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    var toolbarManager: ToolbarManager? = null

    @Inject
    lateinit var viewModeFactory: DaggerViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarManager = ToolbarManager(initToolbar(), view).apply { prepareToolbar() }
        initViews()
    }

    protected abstract fun injectDependencies(): Unit?

    protected abstract fun clearInjectedComponents(): Unit?

    protected abstract fun initToolbar(): FragmentToolbar

    protected abstract fun bindViewModel(): Unit?

    protected open fun initViews() {}

    override fun onDestroyView() {
        toolbarManager = null
        super.onDestroyView()
    }

    override fun onDetach() {
        clearInjectedComponents()
        super.onDetach()
    }
}