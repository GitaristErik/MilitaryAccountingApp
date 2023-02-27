package com.example.militaryaccountingapp.presenter.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.militaryaccountingapp.databinding.FragmentHomeCategoriesBinding

class HomeCategoriesFragment : Fragment() {

    private lateinit var binding: FragmentHomeCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeCategoriesBinding.inflate(layoutInflater, container, false)

        binding.rvCategories.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = TempAdapter(
                Array(20) { (it + 1).toString() }
            )
        }

        return binding.root
    }
}