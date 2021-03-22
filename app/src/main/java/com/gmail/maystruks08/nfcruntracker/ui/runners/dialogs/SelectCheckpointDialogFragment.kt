package com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseDialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.databinding.DialogSelectCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import java.util.ArrayList

class SelectCheckpointDialogFragment : BaseDialogFragment(), CheckpointAdapter.Interaction {

    private lateinit var binding: DialogSelectCheckpointBinding
    private var callback: ((checkpointView: CheckpointView) -> Unit)? = null
    private var checkpoints: ArrayList<CheckpointView> by argument()

    private var checkpointAdapter: CheckpointAdapter? = null

    override val dialogWidth: Int = R.dimen.dialog_width_standard
    override val dialogHeight: Int = R.dimen.dialog_height_standard

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        checkpointAdapter = CheckpointAdapter(this)

    return DialogSelectCheckpointBinding.inflate(inflater, container, false)
        .let {
            binding = it
            return@let it.root
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
        with(binding){
            rvCheckpoints.adapter = checkpointAdapter
            checkpointAdapter?.checkpoints = checkpoints
            btnClose.setOnClickListener { dismiss() }
        }
    }

    override fun onClickAtCheckpoint(checkpointView: CheckpointView) {
        callback?.invoke(checkpointView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCheckpoints.adapter = null
        callback = null
    }

    companion object {

        fun getInstance(
            checkpoints: ArrayList<CheckpointView>,
            callback: (checkpointView: CheckpointView) -> Unit
        ) = SelectCheckpointDialogFragment().apply {
            this.checkpoints = checkpoints
            this.callback = callback
        }
    }
}