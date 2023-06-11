package com.example.militaryaccountingapp.presenter.fragment.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentTreeViewBinding
import com.example.militaryaccountingapp.databinding.ItemTreeNodeViewBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.gg.gapo.treeviewlib.GapoTreeView
import com.gg.gapo.treeviewlib.model.NodeState
import com.gg.gapo.treeviewlib.model.NodeViewData

class TreeViewFragment() :
    BaseViewModelFragment<FragmentTreeViewBinding, FilterViewModel.ViewData, FilterViewModel>(),
    GapoTreeView.Listener<TreeNodeItem> {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTreeViewBinding =
        FragmentTreeViewBinding::inflate

    override val viewModel: FilterViewModel by activityViewModels()

    override fun initializeView() {
    }


    private val treeViewBuilder by lazy {
        GapoTreeView.Builder.plant<TreeNodeItem>(requireContext())
            .withRecyclerView(binding.rvItems)
            .withLayoutRes(R.layout.item_tree_node_view)
            .setListener(this)
            .itemMargin(20)
    }

    private lateinit var treeView: GapoTreeView<TreeNodeItem>

    override suspend fun observeCustomData() {
        viewModel.dataNodes
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect {
                renderTreeView(it)
            }
    }

    override fun render(data: FilterViewModel.ViewData) {
        viewModelHandler = null
        data.selectedItemsIds.forEach {
            treeView.selectNode(it.toString(), true)
        }
        data.selectedCategoriesIds.forEach {
            treeView.selectNode(it.toString(), true)
        }
        viewModelHandler = ::viewModelHandlerImpl
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
        viewModelHandler?.invoke(node, child, isSelected)

        //update layout
        treeView.requestUpdateTree()
    }

    private var viewModelHandler: ((
        node: NodeViewData<TreeNodeItem>,
        child: List<NodeViewData<TreeNodeItem>>,
        isSelected: Boolean
    ) -> Unit)? = null

    private fun viewModelHandlerImpl(
        node: NodeViewData<TreeNodeItem>,
        child: List<NodeViewData<TreeNodeItem>>,
        isSelected: Boolean
    ): Unit = with(node.getData()) {
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


    companion object {
        /** Customize node state **/
        object NodeStateDisabled : NodeState()

    }
}