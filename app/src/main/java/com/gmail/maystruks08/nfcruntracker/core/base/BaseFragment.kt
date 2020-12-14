package com.gmail.maystruks08.nfcruntracker.core.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    var toolbarManager: ToolbarManager? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarManager = ToolbarManager(initToolbar(), view).apply { prepareToolbar() }
        initViews()
    }

    protected abstract fun initToolbar(): FragmentToolbar

    protected abstract fun bindViewModel(): Unit?

    protected open fun initViews() {}

    protected fun hideSoftKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onStop() {
        hideSoftKeyboard()
        super.onStop()
    }

    override fun onDestroyView() {
        toolbarManager = null
        super.onDestroyView()
    }
}