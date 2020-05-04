package com.gmail.maystruks08.nfcruntracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.gmail.maystruks08.domain.toDateTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.ext.getFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.navigation.AppNavigator
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.runners.RootRunnersFragment
import com.gmail.maystruks08.nfcruntracker.utils.NfcAdapter
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Command
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val PRESS_TWICE_INTERVAL = 2000

class HostActivity : AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    private var lastBackPressTime = 0L

    private val nfcAdapter = NfcAdapter()

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        App.baseComponent.inject(this)

        router.newRootScreen(Screens.LoginScreen())
    }

    private val navigator: Navigator =
        object : AppNavigator(this, supportFragmentManager, R.id.nav_host_container) {
            override fun setupFragmentTransaction(
                command: Command?,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    override fun onBackPressed() {
        this.hideSoftKeyboard()
        this.navigateBack()
    }

    private fun navigateBack() {
        when {
            supportFragmentManager.backStackEntryCount > 0 -> router.exit()
            lastBackPressTime < System.currentTimeMillis() - PRESS_TWICE_INTERVAL -> {
                Toast.makeText(this, R.string.toast_exit_app_warning_text, Toast.LENGTH_SHORT)
                    .show()
                lastBackPressTime = System.currentTimeMillis()
            }
            else -> router.exit()
        }
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
        nfcAdapter.startListening(this)
        if (!nfcAdapter.isEnabled()) {
            val builder = AlertDialog.Builder(this)
                .setTitle("NFC отключен!")
                .setMessage("Включить NFC для сканирования карт участников?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    toast("Пожалуйста, активируйте NFC и нажмите Назад, чтобы вернуться в приложение!")
                    alertDialog?.dismiss()
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    toast("NFC сканер отключен! Некоторые функции не будут работать!")
                    alertDialog?.dismiss()
                }
            alertDialog = builder.show()
        } else alertDialog?.dismiss()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcAdapter.processReadCard(intent)?.let { cardId ->
            Timber.log(Log.INFO, "Card scanned: $cardId at ${Date().toDateTimeFormat()}")
            toast("Карта: $cardId")
            getFragment<RootRunnersFragment>(Screens.RootRunnersScreen.tag())?.onNfcCardScanned(cardId)
        }
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
        nfcAdapter.stopListening(this)
        alertDialog?.dismiss()
    }
}
