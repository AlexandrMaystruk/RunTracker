package com.gmail.maystruks08.nfcruntracker.ui.main.utils

import android.view.View
import androidx.core.content.ContextCompat
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.CircleMenuLayoutBinding

class CircleMenuManager(
    private val circleMenuLayoutBinding: CircleMenuLayoutBinding,
    private val callback: (CircleMenuEvent) -> Unit
) {

    private var isChildVisible = false

    private var qdCOdeButtonPosition: Pair<Float, Float>
    private var btnRegisterNewRunnerPosition: Pair<Float, Float>

    init {
        with(circleMenuLayoutBinding) {
            qdCOdeButtonPosition = btnScanQrCode.x to btnScanQrCode.y
            btnRegisterNewRunnerPosition = btnRegisterNewRunner.x to btnRegisterNewRunner.y
            btnMenu.setOnClickListener {
                if (isChildVisible) hideChild() else showChild()
            }
            hideChild()
        }
    }

    private fun showChild() {
        with(circleMenuLayoutBinding) {
            inAnimationTransition()
            btnMenu.rotation = 45f
            btnMenu.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_add))
            with(btnScanQrCode) {
                visibility = View.VISIBLE
                x = qdCOdeButtonPosition.first
                y = qdCOdeButtonPosition.second
                setOnClickListener { callback.invoke(CircleMenuEvent.CLICKED_SCAN_QR_CODE) }
            }

            with(btnRegisterNewRunner) {
                visibility = View.VISIBLE
                x = btnRegisterNewRunnerPosition.first
                y = btnRegisterNewRunnerPosition.second
                setOnClickListener { callback.invoke(CircleMenuEvent.CLICKED_REGISTER_NEW_RUNNER) }
            }
            isChildVisible = true
        }
    }

    private fun hideChild() {
        with(circleMenuLayoutBinding) {
            outAnimationTransition()
            btnMenu.rotation = 0f
            btnMenu.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_menu))
            with(btnScanQrCode) {
                qdCOdeButtonPosition = x to y
                x = btnMenu.x
                y = btnMenu.y
                visibility = View.GONE
                setOnClickListener(null)
            }

            with(btnRegisterNewRunner) {
                btnRegisterNewRunnerPosition = x to y
                x = btnMenu.x
                y = btnMenu.y
                visibility = View.GONE
                setOnClickListener(null)
            }
            isChildVisible = false
        }
    }

    private fun inAnimationTransition() {
        val transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(Fade(Fade.IN))
        TransitionManager.beginDelayedTransition(circleMenuLayoutBinding.circleMenu, transition)
    }

    private fun outAnimationTransition() {
        val transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(Fade(Fade.OUT))
        TransitionManager.beginDelayedTransition(circleMenuLayoutBinding.circleMenu, transition)
    }
}

enum class CircleMenuEvent {

    CLICKED_REGISTER_NEW_RUNNER,
    CLICKED_SCAN_QR_CODE

}