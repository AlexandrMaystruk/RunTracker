package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView

class ScreenSlidePagerAdapter(
    private val onClickedAtRunner: (RunnerView) -> Unit,
    fm: FragmentManager
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val registeredFragments: SparseArray<RunnersFragment> = SparseArray()

    override fun getCount(): Int = RunnerType.values().size

    override fun getItem(position: Int): Fragment =
        RunnersFragment.getInstance(position, onClickedAtRunner)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as RunnersFragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getRegisteredFragment(position: Int): RunnersFragment = registeredFragments[position]

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> "Бегуны"
        1 -> "Железные"
        else -> ""
    }


    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        try {
            super.restoreState(state, loader)
        } catch (e: Exception) {
            Log.e("ScreenSlidePagerAdapter", "Error Restore State of Fragment : " + e.message, e)
        }
    }
}