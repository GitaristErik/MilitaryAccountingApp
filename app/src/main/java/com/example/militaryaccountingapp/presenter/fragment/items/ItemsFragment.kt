package com.example.militaryaccountingapp.presenter.fragment.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.militaryaccountingapp.databinding.FragmentItemsBinding

class ItemsFragment : Fragment() {

    private lateinit var binding: FragmentItemsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}