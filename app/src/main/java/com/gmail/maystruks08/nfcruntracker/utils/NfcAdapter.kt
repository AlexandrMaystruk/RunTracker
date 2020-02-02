package com.gmail.maystruks08.nfcruntracker.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import java.math.BigInteger

class NfcAdapter {

    private var nfcAdapter: NfcAdapter? = null


    fun isEnabled() = nfcAdapter?.isEnabled == true

    fun startListening(activity: Activity) {
        val intent = Intent(activity, activity.javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)
        val intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        val techListsArray = arrayOf(
                arrayOf(NfcA::class.java.name),
                arrayOf(NfcF::class.java.name),
                arrayOf(NfcB::class.java.name),
                arrayOf(NfcV::class.java.name),
                arrayOf(IsoDep::class.java.name),
                arrayOf(Ndef::class.java.name),
                arrayOf(NdefFormatable::class.java.name),
                arrayOf(MifareClassic::class.java.name),
                arrayOf(MifareUltralight::class.java.name)
        )
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techListsArray)
    }

    fun stopListening(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
        nfcAdapter = null
    }

    fun processReadCard(intent: Intent): String? {
        val id = intent.getParcelableExtra<Tag?>(NfcAdapter.EXTRA_TAG)?.id
        return id?.let { String.format("%0${it.size * 2}X", BigInteger(1, it)) }
    }
}