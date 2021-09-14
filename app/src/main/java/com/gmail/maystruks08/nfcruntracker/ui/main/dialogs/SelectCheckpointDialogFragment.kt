package com.gmail.maystruks08.nfcruntracker.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseDialogFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.databinding.DialogSelectCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SelectCheckpointDialogFragment : BaseDialogFragment(), CheckpointAdapter.Interaction {

    private lateinit var binding: DialogSelectCheckpointBinding
    private var callback: ((checkpointView: CheckpointView) -> Unit)? = null
    private var raceId: String by argument()
    private var distanceId: String by argument()

    private val viewModel: SelectCheckpointDialogViewModel by viewModels()

    private var checkpointAdapter: CheckpointAdapter? = null

    override val dialogWidth: Int = R.dimen.dialog_width_standard
    override val dialogHeight: Int = R.dimen.dialog_height_standard


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkpointAdapter = CheckpointAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        checkpointAdapter = CheckpointAdapter(this)
        viewModel.init(CurrentRaceDistance(raceId, distanceId))
        return DialogSelectCheckpointBinding.inflate(inflater, container, false)
            .let {
                binding = it
                return@let it.root
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.color.colorTransparent
                    )
                )
                attributes?.windowAnimations = R.style.DialogAnimation
            }
        }
    }

    override fun initViews() {
        with(binding) {
            rvCheckpoints.adapter = checkpointAdapter
            btnClose.setOnClickListener { dismiss() }
        }
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                showCheckpoints.collect { checkpointAdapter?.checkpoints = it }
            }
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
            raceId: String,
            distanceId: String,
            callback: (checkpointView: CheckpointView) -> Unit
        ) = SelectCheckpointDialogFragment().apply {
            this.raceId = raceId
            this.distanceId = distanceId
            this.callback = callback
        }
    }
}