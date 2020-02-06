package com.gmail.maystruks08.nfcruntracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.gmail.maystruks08.nfcruntracker.core.ext.getFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.navigation.AppNavigator
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import com.gmail.maystruks08.nfcruntracker.utils.NfcAdapter
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Command
import javax.inject.Inject

const val PRESS_TWICE_INTERVAL = 2000

class HostActivity : AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    private var lastBackPressTime = 0L

    private val nfcAdapter = NfcAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
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
            builder.setTitle("NFC отключен!")
            builder.setMessage("Включить NFC для сканирования карт участников?")
            builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
                toast("Please activate NFC and press Back to return to the application!")
                dialog.dismiss()
                startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
            }
            builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                toast("NFC scanner disabled! Some functions will not work!")
                dialog.dismiss()
            }
            builder.show()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcAdapter.processReadCard(intent)?.let { cardId ->
            toast("Nfc card scanned. Id = $cardId")
            getFragment<RunnersFragment>(Screens.RunnersScreen.tag())?.viewModel?.onNfcCardScanned(cardId)
        }
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
        nfcAdapter.stopListening(this)
    }
}
