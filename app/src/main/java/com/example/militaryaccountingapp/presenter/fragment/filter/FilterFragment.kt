package com.example.militaryaccountingapp.presenter.fragment.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentFiltersBinding
import com.example.militaryaccountingapp.databinding.ItemTreeNodeViewBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersFilterAdapter
import com.gg.gapo.treeviewlib.GapoTreeView
import com.gg.gapo.treeviewlib.model.NodeState
import com.gg.gapo.treeviewlib.model.NodeViewData
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch


class FilterFragment : BaseViewModelFragment<FragmentFiltersBinding, ViewData, FilterViewModel>(),
    GapoTreeView.Listener<TreeNodeItem> {

    override val viewModel: FilterViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFiltersBinding
        get() = FragmentFiltersBinding::inflate

    override fun initializeView() {
        setupAdapter()
        setupTreeView()
        setupDateButtons()
    }

    override fun render(data: ViewData) {
        usersAdapter.submitList(data.usersUi)
    }

    private val usersAdapter by lazy {
        UsersFilterAdapter { user, selected ->
            viewModel.changeUserSelection(user, selected)
        }
    }

    private fun setupAdapter(): Unit = binding.rvUsers.run {
        adapter = usersAdapter
        addItemDecoration(
            MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

    private fun setupDateButtons() {
        binding.dateMode.apply {
            addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
                toggleButton.findViewById<MaterialButton>(checkedId).icon =
                    if (isChecked) ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_check_24dp
                    ) else null
            }
        }
    }

    private val treeViewBuilder by lazy {
        GapoTreeView.Builder.plant<TreeNodeItem>(requireContext())
            .withRecyclerView(binding.rvItems)
            .withLayoutRes(R.layout.item_tree_node_view)
            .setListener(this)
            .itemMargin(20)
    }

    private lateinit var treeView: GapoTreeView<TreeNodeItem>

    private fun setupTreeView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataNodes
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    try {
                        binding.rvItems.removeItemDecorationAt(0)
                    } catch (_: Exception) {
                    }
                    treeView = treeViewBuilder
                        .setData(it)
                        .build()

                    log.d(it.toString())
                }
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
            checkbox.isChecked = item.isSelected
            checkbox.setOnClickListener {
                treeView.selectNode(item.nodeId, !item.isSelected) // will trigger onNodeSelected
            }
            checkbox.isEnabled = item.nodeState != NodeStateDisabled

//            avatarGroup.dataSource = item.getData().userImagesUrl.toMutableList()

            //toggle node
            holder.setOnClickListener {
                if (item.isExpanded) {
                    treeView.collapseNode(item.nodeId)
                } else {
                    treeView.expandNode(item.nodeId)
                }
            }
        }
    }

    override fun onNodeSelected(
        node: NodeViewData<TreeNodeItem>,
        child: List<NodeViewData<TreeNodeItem>>,
        isSelected: Boolean
    ) {
        //set selected for parent node and its child
        treeView.setSelectedNode(arrayListOf(node).apply { addAll(child) }, isSelected)

        //disable all child
        treeView.setNodesState(
            child.map { it.nodeId },
            if (isSelected) NodeStateDisabled else null
        )

        // event for viewModel
        with(node.getData()) {
            if (!node.isLeaf) {
                viewModel.changeCategorySelection(
                    id,
                    child.map { it.getData().id to it.isLeaf },
                    isSelected
                )
            } else {
                viewModel.changeItemSelection(id, isSelected)
            }
        }

        //update layout
        treeView.requestUpdateTree()
    }

    companion object {
        /** Customize node state **/
        object NodeStateDisabled : NodeState()

    }
}