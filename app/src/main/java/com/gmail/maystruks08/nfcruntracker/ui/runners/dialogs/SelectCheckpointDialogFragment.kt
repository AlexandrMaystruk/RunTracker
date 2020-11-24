package com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseDialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import kotlinx.android.synthetic.main.dialog_success.*


class SelectCheckpointDialogFragment : BaseDialogFragment() {

    private var currentCheckpointId: Int by argument()
    private lateinit var callback: (newCheckpointId: Int) -> Unit

    override val viewResource: Int = R.layout.dialog_select_checkpoint
    override val dialogWidth: Int = R.dimen.dialog_width_standard
    override val dialogHeight: Int = R.dimen.dialog_height_standard

    override fun injectDependencies(): Unit? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.color.colorTransparent))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    override fun initViews() {
        tvAlertText.text = "Select checkpoint hardcode text"
        buttonOk.setOnClickListener { dismiss() }
    }

    companion object {

        fun getInstance(currentCheckpointId: Int, callback: (newCheckpointId: Int) -> Unit) = SelectCheckpointDialogFragment().apply {
            this.currentCheckpointId = currentCheckpointId
            this.callback = callback
        }
    }
}