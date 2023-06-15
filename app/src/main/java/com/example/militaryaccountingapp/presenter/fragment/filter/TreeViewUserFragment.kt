package com.example.militaryaccountingapp.presenter.fragment.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentTreeViewBinding
import com.example.militaryaccountingapp.databinding.ItemTreeNodeViewBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.gg.gapo.treeviewlib.GapoTreeView
import com.gg.gapo.treeviewlib.model.NodeViewData
import kotlinx.coroutines.launch

class TreeViewUserFragment() :
    BaseViewModelFragment<FragmentTreeViewBinding, ViewData, DetailsUserViewModel>(),
    GapoTreeView.Listener<TreeNodeItem> {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTreeViewBinding =
        FragmentTreeViewBinding::inflate

    override val viewModel: DetailsUserViewModel by activityViewModels()

    override fun initializeView() {
        observeCustomData2()
    }


    private val treeViewBuilder by lazy {
        GapoTreeView.Builder.plant<TreeNodeItem>(requireContext())
            .withRecyclerView(binding.rvItems)
            .withLayoutRes(R.layout.item_tree_node_view)
            .setListener(this)
            .itemMargin(20)
    }

    private var treeView: GapoTreeView<TreeNodeItem>? = null


    private fun observeCustomData2() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataNodes
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    renderTreeView(it)
                }
        }
    }

    private fun renderTreeView(data: List<TreeNodeItem>) {
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

            title.text = item.getData().name
            checkbox.visibility = View.GONE

            //toggle node
            holder.setOnClickListener {
                if (item.isExpanded) {
                    treeView?.collapseNode(item.nodeId)
                } else {
                    treeView?.expandNode(item.nodeId)
                }
            }
        }
    }

    override fun onNodeSelected(
        node: NodeViewData<TreeNodeItem>,
        child: List<NodeViewData<TreeNodeItem>>,
        isSelected: Boolean
    ) {}
}