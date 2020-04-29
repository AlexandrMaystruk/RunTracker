package com.gmail.maystruks08.nfcruntracker.core.ext

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Context.toast(text: String = "Some text") {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

inline fun <reified T : Fragment> FragmentActivity.getFragment(tag: String): T? {
    return supportFragmentManager.findFragmentByTag(tag) as? T
}

inline fun <reified T : Fragment> Fragment.getVisibleFragment(): T? {
    return fragmentManager?.fragments?.lastOrNull() as? T
}

inline fun <reified T : Fragment> Fragment.getChildVisibleFragment(): T? {
    return childFragmentManager.fragments.lastOrNull() as? T
}

inline fun <reified T : ViewModel> Fragment.injectViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, factory)[T::class.java]
}

fun Any.name(): String = this::class.java.simpleName

@Suppress("UNCHECKED_CAST")
fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        is Boolean -> putBoolean(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Short -> putShort(key, value)
        is Long -> putLong(key, value)
        is Byte -> putByte(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Float -> putFloat(key, value)
        is Bundle -> putBundle(key, value)
        is Parcelable -> putParcelable(key, value)
        is ArrayList<*> -> putParcelableArrayList(key, value as ArrayList<out Parcelable>)
        else -> throw IllegalStateException("Type of property $key is not supported")
    }
}

class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val key = property.name
        return thisRef.arguments?.get(key) as? T
            ?: throw IllegalStateException("Property ${property.name} could not be read")
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        args.put(key, value)
    }
}

fun <T : Any> argument(): ReadWriteProperty<Fragment, T> = FragmentArgumentDelegate()

fun <T : Any> argumentNullable(): ReadWriteProperty<Fragment, T?> =
    FragmentNullableArgumentDelegate()

class FragmentNullableArgumentDelegate<T : Any?> : ReadWriteProperty<Fragment, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        val key = property.name
        return thisRef.arguments?.get(key) as? T
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        value?.let { args.put(key, it) } ?: args.remove(key)
    }
}