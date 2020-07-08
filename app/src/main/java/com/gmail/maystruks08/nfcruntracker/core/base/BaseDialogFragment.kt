package com.gmail.maystruks08.nfcruntracker.core.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.getDisplayHeight
import com.gmail.maystruks08.nfcruntracker.core.ext.getDisplayWidth

abstract class BaseDialogFragment : DialogFragment() {

    protected abstract val viewResource: Int
    protected abstract val dialogWidth: Int
    protected abstract val dialogHeight: Int

    protected abstract fun injectDependencies(): Unit?

    protected abstract fun initViews(): Unit?

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(viewResource, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        val width = (requireContext().getDisplayWidth() * ResourcesCompat.getFloat(resources, dialogWidth)).toInt()
        val height = (requireContext().getDisplayHeight() * ResourcesCompat.getFloat(resources, dialogHeight)).toInt()
        dialog?.window?.setLayout(width, height)
    }

}