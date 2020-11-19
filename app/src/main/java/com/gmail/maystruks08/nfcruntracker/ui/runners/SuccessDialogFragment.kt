package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseDialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import kotlinx.android.synthetic.main.dialog_success.*


class SuccessDialogFragment : BaseDialogFragment() {

    private var message: String by argument()

    override val viewResource: Int = R.layout.dialog_success
    override val dialogWidth: Int = R.dimen.dialog_width
    override val dialogHeight: Int = R.dimen.dialog_height

    override fun injectDependencies(): Unit? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.color.colorTransparent))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    override fun initViews() {
        tvAlertText.text = message
        buttonOk.setOnClickListener { dismiss() }
        view?.postDelayed({ dismiss() }, 3000L)
    }

    companion object {

        fun getInstance(message: String) = SuccessDialogFragment().apply {
            this.message = message
        }
    }
}