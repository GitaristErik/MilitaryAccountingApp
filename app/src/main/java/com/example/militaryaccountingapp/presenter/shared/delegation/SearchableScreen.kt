package com.example.militaryaccountingapp.presenter.shared.delegation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

interface SearchableScreen {
    fun prepareSearch(
        context: Context,
        searchBar: SearchBar?,
        searchView: SearchView,
        searchViewList: RecyclerView,
        updateSearchBarTextOnSearch: Boolean,
        onTypeHandler: (String) -> Unit,
        onCloseHandler: () -> Unit,
        onOpenHandler: () -> Unit,
        onSearchHandler: (String) -> Unit,
    )

    fun updateSearchSuggestion(suggestionData: List<String>)
}