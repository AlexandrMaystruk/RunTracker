package com.gmail.maystruks08.nfcruntracker.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.annotation.Resource

abstract class BaseFragment(@Resource private val layout : Int) : Fragment() {

    var toolbarManager: ToolbarManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

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