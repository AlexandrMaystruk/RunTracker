package com.gmail.maystruks08.nfcruntracker.ui.qr_code

import com.budiyev.android.codescanner.*
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentScanCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ScanCodeFragment : BaseFragment(R.layout.fragment_scan_code) {

    private val binding: FragmentScanCodeBinding by viewBinding()

    private lateinit var codeScanner: CodeScanner

    private lateinit var callback: (scannedCode: String) -> Unit

    @Inject
    lateinit var router: Router

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { router.exit() }
        .withTitle(R.string.screen_scan_qr_code)
        .build()

    override fun bindViewModel() = Unit

    override fun initViews() {
        with(binding) {
            codeScanner = CodeScanner(requireContext(), scannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = CodeScanner.ALL_FORMATS
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false
                decodeCallback = DecodeCallback {
                    activity?.runOnUiThread {
                        callback.invoke(it.text)
                    }
                }
                errorCallback = ErrorCallback {
                    Timber.e(it)
                    router.exit()
                }
            }
            scannerView.setOnClickListener { codeScanner.startPreview() }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onDestroyView() {
        codeScanner.releaseResources()
        super.onDestroyView()
    }

    companion object {

        fun getInstance(callback: (scannedCode: String) -> Unit) = ScanCodeFragment().apply {
            this.callback = callback
        }
    }
}
