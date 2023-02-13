package com.example.militaryaccountingapp.ui.home

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            viewPager.adapter = HomeViewPagerAdapter(this@HomeFragment.requireActivity())

            TabLayoutMediator(
                tabLayout,
                viewPager
            ) { tab, position ->
                when (position) {
                    0 -> tab.text = "Categories"
                    1 -> tab.text = "Items"
                }
            }.attach()
        }



//        binding.searchView.setupWithSearchBar(binding.searchBar)
        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

class TempAdapter(private val items: Array<String>) :
    RecyclerView.Adapter<TempAdapter.Companion.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val listItem: View = LayoutInflater.from(parent.context).inflate(
            R.layout.simple_list_item_2, parent, false
        )
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: String) {
                itemView.findViewById<TextView>(R.id.text1).text = item
            }
        }
    }
}

// adapter for horizontal recycler view in home fragment like a card view with image and text without using card view
class HorizontalAdapter(private val items: Array<String>) :
    RecyclerView.Adapter<HorizontalAdapter.Companion.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val listItem: View = LayoutInflater.from(parent.context).inflate(
            R.layout.simple_list_item_2, parent, false
        )
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: String) {
                itemView.findViewById<TextView>(R.id.text1).text = item
                itemView.findViewById<TextView>(R.id.text2).text = item
            }
        }
    }
}