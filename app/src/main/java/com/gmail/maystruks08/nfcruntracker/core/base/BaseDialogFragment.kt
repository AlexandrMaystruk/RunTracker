package com.gmail.maystruks08.nfcruntracker.core.base

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.getDisplayHeight
import com.gmail.maystruks08.nfcruntracker.core.ext.getDisplayWidth

abstract class BaseDialogFragment : DialogFragment() {

    protected abstract val dialogWidth: Int
    protected abstract val dialogHeight: Int

    protected abstract fun initViews(): Unit?

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