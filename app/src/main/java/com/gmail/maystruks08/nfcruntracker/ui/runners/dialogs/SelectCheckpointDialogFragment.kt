package com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseDialogFragment
import com.gmail.maystruks08.nfcruntracker.databinding.DialogSuccessBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView

class SelectCheckpointDialogFragment : BaseDialogFragment() {

    private lateinit var binding: DialogSuccessBinding
    private lateinit var callback: (checkpointView: CheckpointView) -> Unit
    private lateinit var checkpoints: Array<CheckpointView>


    override val dialogWidth: Int = R.dimen.dialog_width_standard
    override val dialogHeight: Int = R.dimen.dialog_height_standard

    override fun injectDependencies(): Unit? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogSuccessBinding.inflate(inflater, container, false).let { successBinding ->
            binding = successBinding
            binding.root
        }
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
        binding.buttonOk.setOnClickListener { dismiss() }
    }

    companion object {

        fun getInstance(
            checkpoints: Array<CheckpointView>,
            callback: (checkpointView: CheckpointView) -> Unit
        ) = SelectCheckpointDialogFragment().apply {
            this.checkpoints = checkpoints
            this.callback = callback
        }
    }
}