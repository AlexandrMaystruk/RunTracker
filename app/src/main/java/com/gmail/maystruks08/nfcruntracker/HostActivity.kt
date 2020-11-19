package com.gmail.maystruks08.nfcruntracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.toDateTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.DaggerViewModelFactory
import com.gmail.maystruks08.nfcruntracker.core.ext.getFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.navigation.AppNavigator
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import com.gmail.maystruks08.nfcruntracker.utils.NfcAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_host.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val PRESS_TWICE_INTERVAL = 2000

class HostActivity : AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var networkUtil: NetworkUtil

    @Inject
    lateinit var viewModeFactory: DaggerViewModelFactory

    lateinit var viewModel: HostViewModel

    private var lastBackPressTime = 0L

    private val nfcAdapter = NfcAdapter()

    private var alertDialog: AlertDialog? = null

    private var snackBar: Snackbar? = null

    private var toast: Toast? = null

    private val navigator: Navigator = object : AppNavigator(this, supportFragmentManager, R.id.nav_host_container) {
        override fun setupFragmentTransaction(command: Command?, currentFragment: Fragment?, nextFragment: Fragment?, fragmentTransaction: FragmentTransaction) {
            fragmentTransaction.setReorderingAllowed(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.AppTheme, true)
        setContentView(R.layout.activity_host)
        App.hostComponent?.inject(this)

        viewModel = injectViewModel(viewModeFactory)

        viewModel.runnerChange.observe(this, {
            getFragment<RunnersFragment>(Screens.RunnerScreen.tag())?.receiveRunnerUpdateFromServer(it)
        })

        networkUtil.subscribeToConnectionChange(this.javaClass.simpleName) { isConnected ->
            if (!isConnected) {
                snackBar = Snackbar.make(
                    nav_host_container,
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
        if (!nfcAdapter.isEnabled()) {
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcAdapter.processReadCard(intent)?.let { cardId ->
            Timber.log(Log.INFO, "Card scanned: $cardId at ${Date().toDateTimeFormat()}")
            getFragment<RunnersFragment>(Screens.RunnerScreen.tag())?.onNfcCardScanned(cardId)
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
        toast?.cancel()
        networkUtil.unsubscribe(this.javaClass.simpleName)
        toast?.cancel()
        toast = null
        snackBar?.dismiss()
        snackBar = null
        App.clearHostComponent()
        super.onStop()
    }
}
