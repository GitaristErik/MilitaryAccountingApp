package com.example.militaryaccountingapp.presenter.fragment.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentTreeViewUserBinding
import com.example.militaryaccountingapp.databinding.ItemTreeNodeViewBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserFragmentDirections
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.gg.gapo.treeviewlib.GapoTreeView
import com.gg.gapo.treeviewlib.model.NodeViewData
import kotlinx.coroutines.launch

class TreeViewUserFragment() :
    BaseViewModelFragment<FragmentTreeViewUserBinding, ViewData, DetailsUserViewModel>(),
    GapoTreeView.Listener<TreeNodeItem> {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTreeViewUserBinding =
        FragmentTreeViewUserBinding::inflate

    override val viewModel: DetailsUserViewModel by activityViewModels()

    override fun initializeView() {
        observeNodes()
        observeSelfNodes()
    }


    private val treeViewBuilder by lazy {
        GapoTreeView.Builder.plant<TreeNodeItem>(requireContext())
            .withRecyclerView(binding.rvItems)
            .withLayoutRes(R.layout.item_tree_node_view)
            .setListener(this)
            .itemMargin(20)
    }


    private val treeViewSelfBuilder by lazy {
        GapoTreeView.Builder.plant<TreeNodeItem>(requireContext())
            .withRecyclerView(binding.rvItemsSelf)
            .withLayoutRes(R.layout.item_tree_node_view)
            .setListener(this)
            .itemMargin(20)
    }

    private var treeView: GapoTreeView<TreeNodeItem>? = null
    private var treeViewSelf: GapoTreeView<TreeNodeItem>? = null


    private fun observeNodes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataNodes
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    renderTreeView(it)
                }
        }
    }

    private fun observeSelfNodes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataSelfNodes
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    renderTreeViewSelf(it)
                }
        }
    }

    private fun renderTreeViewSelf(data: List<TreeNodeItem>) {
        log.d("renderTreeViewSelf $data")
        try {
            binding.rvItemsSelf.removeItemDecorationAt(0)
        } catch (_: Exception) {
        } finally {
            treeViewSelf = treeViewSelfBuilder
                .setData(data)
                .build()
        }
    }

    private fun renderTreeView(data: List<TreeNodeItem>) {
        log.d("renderTreeView $data")
        try {
            binding.rvItems.removeItemDecorationAt(0)
        } catch (_: Exception) {
        } finally {
            treeView = treeViewBuilder
                .setData(data)
                .build()
        }
    }

    override fun onBind(
        holder: View,
        position: Int,
        item: NodeViewData<TreeNodeItem>,
        bundle: Bundle?
    ) {
        with(ItemTreeNodeViewBinding.bind(holder)) {

            ivArrow.visibility = if (item.isLeaf) View.INVISIBLE else View.VISIBLE
            ivArrow.rotation = if (item.isExpanded) 90f else 0f

            val (name, id) = item.getData().let { it.name to it.id }

            title.text = name
            checkbox.visibility = View.GONE
            redirect.visibility = if (name != "all") View.VISIBLE else View.GONE
            redirect.setOnClickListener {
                findNavController().navigate(
                    if (!item.isLeaf) {
                        DetailsUserFragmentDirections.actionDetailsUserFragmentToCategoryFragment(
                            id = id,
                            name = name
                        )
                    } else {
                        DetailsUserFragmentDirections.actionDetailsUserFragmentToItemFragment(
                            id = id,
                            name = name
                        )
                    }
                )
            }

            //toggle node
            holder.setOnClickListener {
                if (item.isExpanded) {
                    treeView?.collapseNode(item.nodeId)
                    treeViewSelf?.collapseNode(item.nodeId)
                } else {
                    treeView?.expandNode(item.nodeId)
                    treeViewSelf?.expandNode(item.nodeId)
                }
            }
        }
    }

    override fun onNodeSelected(
        node: NodeViewData<TreeNodeItem>,
        child: List<NodeViewData<TreeNodeItem>>,
        isSelected: Boolean
    ) {
    }
}