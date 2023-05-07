package com.example.militaryaccountingapp.presenter.fragment.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    /*    private fun setupTreeView() {
            val nodes: MutableList<TreeNode<*>> = ArrayList()
            val app: TreeNode<CategoryTree> = TreeNode<CategoryTree>(CategoryTree("app"))
            nodes.add(app)
            app.addChild(
                TreeNode(CategoryTree("manifests"))
                    .addChild(TreeNode(ItemTree("AndroidManifest.xml")))
            )
            app.addChild(
                TreeNode(CategoryTree("java")).addChild(
                    TreeNode(CategoryTree("tellh")).addChild(
                        TreeNode(CategoryTree("com")).addChild(
                            TreeNode(CategoryTree("recyclertreeview"))
                                .addChild(TreeNode(ItemTree("CategoryTree")))
                                .addChild(TreeNode(ItemTree("CategoryTreeectoryNodeBinder")))
                                .addChild(TreeNode(ItemTree("ItemTree")))
                                .addChild(TreeNode(ItemTree("ItemTreeNodeBinder")))
                                .addChild(TreeNode(ItemTree("TreeViewBinder")))
                        )
                    )
                )
            )
            val res: TreeNode<CategoryTree> = TreeNode<CategoryTree>(CategoryTree("res"))
            nodes.add(res)
            res.addChild(
                TreeNode(CategoryTree("layout"))
                    .addChild(TreeNode(ItemTree("activity_main.xml")))
                    .addChild(TreeNode(ItemTree("item_CategoryTree.xml")))
                    .addChild(TreeNode(ItemTree("item_ItemTree.xml")))
            )
            res.addChild(
                TreeNode(CategoryTree("mipmap"))
                    .addChild(TreeNode(ItemTree("ic_launcher.png")))
            )

            binding.rvItems.adapter = TreeViewAdapter(
                nodes, listOf(
                    CategoryNodeBinder(),
                    ItemNodeBinder(),
                )
            ).apply {
                setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener {
                    override fun onClick(node: TreeNode<*>, holder: RecyclerView.ViewHolder): Boolean {
                        if (!node.isLeaf) {
                            //Update and toggle the node.
                            onToggle(!node.isExpand, holder)
                        }
                        return false
                    }

                    override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder) {
                        val categoryHolder = holder as CategoryNodeBinder.ViewHolder
                        categoryHolder.setExpandState(isExpand)
                    }
                })
            }
        }*/

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