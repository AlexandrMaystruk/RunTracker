package com.gmail.maystruks08.nfcruntracker.ui.main.utils

import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.CircleMenuLayoutBinding
import kotlin.properties.Delegates


class CircleMenuManager(
    private val circleMenuLayoutBinding: CircleMenuLayoutBinding,
    private val callback: (CircleMenuEvent) -> Unit
) {
    private var _isChildVisible = true

    var isRaceStarted: Boolean by Delegates.observable(false) { _, _, newValue ->
        with(circleMenuLayoutBinding) {
            if (newValue) {
                btnMenu.setOnClickListener { if (_isChildVisible) hideChild() else showChild() }
                hideChild(R.drawable.ic_menu)
                return@with
            }
            btnMenu.setOnClickListener { callback.invoke(CircleMenuEvent.CLICKED_START_RACE) }
            hideChild(R.drawable.ic_play_arrow)
        }
    }

    init {
        with(circleMenuLayoutBinding) {
            btnScanQrCode.setOnClickListener { callback.invoke(CircleMenuEvent.CLICKED_SCAN_QR_CODE) }
            btnRegisterNewRunner.setOnClickListener { callback.invoke(CircleMenuEvent.CLICKED_REGISTER_NEW_RUNNER) }
            if (isRaceStarted) {
                btnMenu.setOnClickListener { if (_isChildVisible) hideChild() else showChild() }
                hideChild(R.drawable.ic_menu)
                return@with
            }
            btnMenu.setOnClickListener { callback.invoke(CircleMenuEvent.CLICKED_START_RACE) }
            hideChild(R.drawable.ic_play_arrow)
        }
    }

    private fun showChild() {
        _isChildVisible = true
        with(circleMenuLayoutBinding) {
            inAnimationTransition()
            btnMenu.rotation = 45f
            btnMenu.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_add))

            val layoutParamsBtnScanQrCode = btnScanQrCode.layoutParams as ConstraintLayout.LayoutParams
            layoutParamsBtnScanQrCode.circleRadius = root.resources.displayMetrics.toPx( 75)
            btnScanQrCode.layoutParams = layoutParamsBtnScanQrCode
            btnScanQrCode.isEnabled = true
            btnScanQrCode.isFocusable = true

            val layoutParamsBtnRegisterNewRunner = btnRegisterNewRunner.layoutParams as ConstraintLayout.LayoutParams
            layoutParamsBtnRegisterNewRunner.circleRadius = root.resources.displayMetrics.toPx( 75)
            btnRegisterNewRunner.layoutParams = layoutParamsBtnRegisterNewRunner
            btnRegisterNewRunner.isEnabled = true
            btnRegisterNewRunner.isFocusable = true
        }
    }

    private fun hideChild(@DrawableRes imageId: Int = R.drawable.ic_menu) {
        _isChildVisible = false
        with(circleMenuLayoutBinding) {
            outAnimationTransition()
            btnMenu.rotation = 0f
            btnMenu.setImageDrawable(ContextCompat.getDrawable(root.context, imageId))

            val layoutParamsBtnScanQrCode = btnScanQrCode.layoutParams as ConstraintLayout.LayoutParams
            layoutParamsBtnScanQrCode.circleRadius = 0
            btnScanQrCode.layoutParams = layoutParamsBtnScanQrCode
            btnScanQrCode.isEnabled = false
            btnScanQrCode.isFocusable = false

            val layoutParamsBtnRegisterNewRunner = btnRegisterNewRunner.layoutParams as ConstraintLayout.LayoutParams
            layoutParamsBtnRegisterNewRunner.circleRadius = 0
            btnRegisterNewRunner.layoutParams = layoutParamsBtnRegisterNewRunner
            btnScanQrCode.isEnabled = false
            btnRegisterNewRunner.isFocusable = false
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

    private fun DisplayMetrics.toPx(dp: Int): Int = (dp * (this.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}

enum class CircleMenuEvent {

    CLICKED_REGISTER_NEW_RUNNER,
    CLICKED_SCAN_QR_CODE,
    CLICKED_START_RACE

}