package com.example.militaryaccountingapp.presenter.shared.delegation

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

interface SearchableScreenContract {

    fun showSearch()

    fun hideSearch()

    fun getSearchView(): SearchView

    fun getSearchBar(): SearchBar

    fun searchViewList(): RecyclerView

}