package com.example.militaryaccountingapp.presenter.shared.delegation.implementation

import android.content.Context
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.presenter.shared.delegation.SearchableScreen
import com.example.militaryaccountingapp.presenter.utils.common.ext.toTitleCase
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionState.HIDING
import com.google.android.material.search.SearchView.TransitionState.SHOWN

class SearchableScreenImpl : SearchableScreen {
//    private lateinit var suggestionTextAdapter: SuggestionTextAdapter

    override fun prepareSearch(
        context: Context,
        searchBar: SearchBar?,
        searchView: SearchView,
        searchViewList: RecyclerView,
        updateSearchBarTextOnSearch: Boolean,
        onTypeHandler: (String) -> Unit,
        onCloseHandler: () -> Unit,
        onOpenHandler: () -> Unit,
        onSearchHandler: (String) -> Unit,
    ) {
        /*suggestionTextAdapter = SuggestionTextAdapter {
            if (updateSearchBarTextOnSearch) searchBar?.text = it.toTitleCase()
            searchView.hide()
            onSearchHandler(it)
        }*/
        searchView.editText.addTextChangedListener { onTypeHandler(searchView.text.toString()) }
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            val text = searchView.text?.toString().orEmpty().trim()
            if (text.isNotEmpty()) {
                if (updateSearchBarTextOnSearch) searchBar?.text = text.toTitleCase()
                searchView.hide()
                onSearchHandler(text)
            }
            false
        }
        searchView.addTransitionListener { _, _, newState ->
            if (newState == HIDING) {
                onCloseHandler()
            } else if (newState == SHOWN) onOpenHandler()
        }
//        searchViewList.adapter = suggestionTextAdapter
//        searchViewList.layoutManager = LinearLayoutManager(context)
    }

    override fun updateSearchSuggestion(suggestionData: List<String>) {
//        suggestionTextAdapter.update(suggestionData)
    }
}