package com.example.militaryaccountingapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeItemsBinding

class HomeItemsFragment : Fragment() {

    private lateinit var binding: FragmentHomeItemsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeItemsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}