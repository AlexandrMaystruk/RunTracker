package com.gmail.maystruks08.nfcruntracker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.toDateTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.ext.getFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.navigation.AppNavigator
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.databinding.ActivityHostBinding
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.main.MainScreenFragment
import com.gmail.maystruks08.nfcruntracker.utils.NfcAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val PRESS_TWICE_INTERVAL = 2000

@FlowPreview
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@AndroidEntryPoint
class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var networkUtil: NetworkUtil

    private val viewModel: HostViewModel by viewModels()

    private var lastBackPressTime = 0L

    private val nfcAdapter = NfcAdapter()

    private var alertDialog: AlertDialog? = null

    private var snackBar: Snackbar? = null

    private var toast: Toast? = null

    private var isFirstLaunch = true

    private var isCorrectFragment = false

    private val navigator: Navigator = object : AppNavigator(this, supportFragmentManager, R.id.nav_host_container) {
        override fun setupFragmentTransaction(command: Command?, currentFragment: Fragment?, nextFragment: Fragment?, fragmentTransaction: FragmentTransaction) {
            isCorrectFragment = nextFragment !is LoginFragment
            fragmentTransaction.setReorderingAllowed(true)
        }
    }

    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.AppTheme, true)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.toast.observe(this, { toast(it) })

        networkUtil.subscribeToConnectionChange(this) { isConnected ->
            if (!isConnected) {
                snackBar = Snackbar.make(
                    binding.navHostContainer,
                    getString(R.string.device_offline),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackBar?.view?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    ?.apply {
                        isSingleLine = false
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }
                snackBar?.show()
            } else {
                snackBar?.dismiss()
            }
        }
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), CAMERA_REQUEST_CODE)
    }

    override fun onBackPressed() {
        this.hideSoftKeyboard()
        this.navigateBack()
    }

    private fun navigateBack() {
        when {
            supportFragmentManager.backStackEntryCount > 0 -> viewModel.exit()
            lastBackPressTime < System.currentTimeMillis() - PRESS_TWICE_INTERVAL -> {
                toast = Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_exit_app_warning_text),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
                lastBackPressTime = System.currentTimeMillis()
            }
            else -> viewModel.exit()
        }
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
        nfcAdapter.startListening(this)
        if (!nfcAdapter.isEnabled() && isFirstLaunch && isCorrectFragment) {
            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.nfc_disabled))
                .setMessage(getString(R.string.is_enable_nfc))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    toast()
                    alertDialog?.dismiss()
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    toast(getString(R.string.nfc_disabled_full_message))
                    alertDialog?.dismiss()
                }
            alertDialog = builder.show()
        } else alertDialog?.dismiss()
        isFirstLaunch = false
    }

    @ObsoleteCoroutinesApi
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcAdapter.processReadCard(intent)?.let { cardId ->
            Timber.log(Log.INFO, "Card scanned: $cardId at ${Date().toDateTimeFormat()}")
            getFragment<MainScreenFragment>(Screens.MainScreen.tag())?.onNfcCardScanned(cardId)
            getFragment<RunnerFragment>(Screens.RunnerScreen.tag())?.onNfcCardScanned(cardId)
        }
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
        nfcAdapter.stopListening(this)
        alertDialog?.dismiss()
        this.hideSoftKeyboard()
    }

    override fun onStop() {
        networkUtil.unsubscribe(this)
        toast?.cancel()
        toast = null
        snackBar?.dismiss()
        snackBar = null
        super.onStop()
    }

    companion object{

        private const val CAMERA_REQUEST_CODE = 1111

    }
}
