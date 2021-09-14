package com.gmail.maystruks08.nfcruntracker.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseDialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.databinding.DialogSuccessBinding


class SuccessDialogFragment : BaseDialogFragment() {

    private lateinit var binding: DialogSuccessBinding

    private var message: String by argument()

    override val dialogWidth: Int = R.dimen.dialog_width
    override val dialogHeight: Int = R.dimen.dialog_height

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogSuccessBinding.inflate(inflater, container, false)
        .let {
            binding = it
            return@let it.root
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.color.colorTransparent
                )
            )
            window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun initViews() {
        with(binding) {
            tvAlertText.text = message
            buttonOk.setOnClickListener { dismiss() }
            root.postDelayed({ dismiss() }, 2000L)
        }
    }

    companion object {

        fun getInstance(message: String) = SuccessDialogFragment().apply {
            this.message = message
        }
    }
}