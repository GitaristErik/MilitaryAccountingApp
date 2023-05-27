package com.example.militaryaccountingapp.presenter.fragment.filter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.militaryaccountingapp.databinding.FragmentTreeViewBinding
import com.example.militaryaccountingapp.databinding.ItemTreeNodeViewBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseFragment
import io.github.dingyi222666.view.treeview.Branch
import io.github.dingyi222666.view.treeview.CreateDataScope
import io.github.dingyi222666.view.treeview.DataSource
import io.github.dingyi222666.view.treeview.Leaf
import io.github.dingyi222666.view.treeview.Tree
import io.github.dingyi222666.view.treeview.TreeNode
import io.github.dingyi222666.view.treeview.TreeNodeEventListener
import io.github.dingyi222666.view.treeview.TreeView
import io.github.dingyi222666.view.treeview.TreeViewBinder
import io.github.dingyi222666.view.treeview.buildTree
import kotlinx.coroutines.launch
import java.util.UUID

class NewTreeViewFragment() : BaseFragment<FragmentTreeViewBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTreeViewBinding =
        FragmentTreeViewBinding::inflate

    private val viewModel: FilterViewModel by activityViewModels()


    override fun initializeView() {
        setupTreeView()
    }

    private fun createTree(): Tree<DataSource<String>> {
        val dataCreator: CreateDataScope<String> = { _, _ -> UUID.randomUUID().toString() }
        return buildTree(dataCreator) {
            Branch("app") {
                Branch("src") {
                    Branch("main") {
                        Branch("java") {
                            Branch("com.dingyi.treeview") {
                                Leaf("MainActivity.kt")
                            }
                        }
                        Branch("res") {
                            Branch("drawable") {

                            }
                            Branch("xml") {}
                        }
                        Leaf("AndroidManifest.xml")
                    }
                }
            }
        }
    }

    private fun setupTreeView() {
        val tree = createTree()

        (binding.treeview as TreeView<DataSource<String>>).apply {
            supportHorizontalScroll = true
            bindCoroutineScope(lifecycleScope)
            this.tree = tree
            binder = ViewBinder()
            nodeEventListener = binder as ViewBinder
            selectionMode = TreeView.SelectionMode.MULTIPLE
        }

        lifecycleScope.launch {
            binding.treeview.refresh()
        }
    }

    inner class ViewBinder : TreeViewBinder<DataSource<String>>(),
        TreeNodeEventListener<DataSource<String>> {

        override fun createView(parent: ViewGroup, viewType: Int): View {
            return ItemTreeNodeViewBinding.inflate(layoutInflater, parent, false).root
        }

        override fun getItemViewType(node: TreeNode<DataSource<String>>): Int =
            if (node.isChild) 1 else 0

        override fun bindView(
            holder: TreeView.ViewHolder,
            node: TreeNode<DataSource<String>>,
            listener: TreeNodeEventListener<DataSource<String>>
        ) {
            ItemTreeNodeViewBinding.bind(holder.itemView).let {
                it.title.text = node.name.toString()
                it.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = node.depth * 22.dp
                }
                if (node.isChild) applyDir(holder, node)
                else it.ivArrow.visibility = View.INVISIBLE
            }
        }

        private fun applyDir(
            holder: TreeView.ViewHolder,
            node: TreeNode<DataSource<String>>
        ) = with(ItemTreeNodeViewBinding.bind(holder.itemView)) {
            ivArrow.also { it.visibility = View.VISIBLE }
                .animate()
                .rotation(if (node.expand) 90f else 0f)
                .setDuration(200)
                .start()
        }


        override fun onClick(node: TreeNode<DataSource<String>>, holder: TreeView.ViewHolder) {
            if (node.isChild) {
                applyDir(holder, node)
            } else {
                Toast.makeText(requireContext(), "Clicked ${node.name}", Toast.LENGTH_LONG).show()
            }
        }

        override fun getCheckableView(
            node: TreeNode<DataSource<String>>,
            holder: TreeView.ViewHolder
        ): Checkable = ItemTreeNodeViewBinding.bind(holder.itemView).checkbox

        override fun onToggle(
            node: TreeNode<DataSource<String>>,
            isExpand: Boolean,
            holder: TreeView.ViewHolder
        ) {
            applyDir(holder, node)
        }
    }
}

inline val Int.dp: Int
    get() = (Resources.getSystem().displayMetrics.density * this + 0.5f).toInt()