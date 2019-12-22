package com.gmail.maystruks08.nfcruntracker.core.base

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.hikingfood.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.R

class ToolbarManager constructor(private var builder: FragmentToolbar, private var container: View) {

    private var fragmentToolbar: Toolbar? = null

    fun prepareToolbar() {
        if (builder.resId != FragmentToolbar.NO_TOOLBAR) {
            fragmentToolbar = container.findViewById(builder.resId) as Toolbar

            if (builder.title != -1) {
                fragmentToolbar?.setTitle(builder.title)
                fragmentToolbar?.setTitleTextColor(ContextCompat.getColor(container.context, R.color.colorAccent))
            }

            if(builder.navigationIcon != -1){
                fragmentToolbar?.setNavigationIcon(builder.navigationIcon)
                builder.navigationIconClickListener?.let { listener ->
                    fragmentToolbar?.setNavigationOnClickListener{
                        listener.invoke()
                    }
                }
            }

            if (builder.menuId != -1) {
                fragmentToolbar?.inflateMenu(builder.menuId)
            }

            if(builder.searchViewTextChangeListener != null){
                val menu = fragmentToolbar?.menu
                val searchView = menu?.findItem(R.id.action_search)?.actionView as? SearchView
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        builder.searchViewTextChangeListener?.invoke(newText)
                        return false
                    }
                })
            }

            if (builder.menuItems.isNotEmpty() && builder.menuClicks.isNotEmpty()){
                val menu = fragmentToolbar?.menu
                for ((index, menuItemId) in builder.menuItems.withIndex()) {
                    menu?.findItem(menuItemId)?.setOnMenuItemClickListener(builder.menuClicks[index])
                }
            }
        }
    }

    fun changeToolbarTitle(title: String){
        fragmentToolbar?.title = title
    }
}