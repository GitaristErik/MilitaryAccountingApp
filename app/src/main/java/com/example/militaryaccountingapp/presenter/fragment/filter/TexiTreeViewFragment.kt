package com.example.militaryaccountingapp.presenter.fragment.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentTreeViewBinding
import com.example.militaryaccountingapp.databinding.ItemTreeNodeViewBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseFragment
import me.texy.treeview.TreeNode
import me.texy.treeview.TreeView
import me.texy.treeview.base.BaseNodeViewBinder
import me.texy.treeview.base.BaseNodeViewFactory
import me.texy.treeview.base.CheckableNodeViewBinder
import timber.log.Timber


class TexiTreeViewFragment() : BaseFragment<FragmentTreeViewBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTreeViewBinding =
        FragmentTreeViewBinding::inflate

    private val viewModel: FilterViewModel by activityViewModels()

    override fun initializeView() {
        setupTreeView()
    }

    private fun setupTreeView() {
        buildTree()
        val view = treeView.view.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        binding.container.addView(view)
    }

    class FirstLevelNodeViewBinder(itemView: View) : CheckableNodeViewBinder(itemView) {

        override fun getLayoutId(): Int = R.layout.item_tree_node_view

        override fun bindView(treeNode: TreeNode) {
            with(ItemTreeNodeViewBinding.bind(itemView)) {
                title.text = treeNode.value.toString()
            }
        }

        override fun getCheckableViewId(): Int = R.id.checkbox
    }

    class MyNodeViewFactory : BaseNodeViewFactory() {
        override fun getNodeViewBinder(
            view: View,
            level: Int
        ): BaseNodeViewBinder {
            Timber.d("view = $view")
            return FirstLevelNodeViewBinder(view)
        }
    }

    val root: TreeNode by lazy {
        TreeNode.root()
    }

    private val treeView: TreeView by lazy {
        TreeView(root, requireContext(), MyNodeViewFactory())
    }

    private val selectedNodes: String
        get() {
            val stringBuilder = StringBuilder("You have selected: ")
            val selectedNodes = treeView.selectedNodes
            for (i in selectedNodes.indices) {
                if (i < 5) {
                    stringBuilder.append(selectedNodes[i].value.toString() + ",")
                } else {
                    stringBuilder.append("...and " + (selectedNodes.size - 5) + " more.")
                    break
                }
            }
            return stringBuilder.toString()
        }

    private fun buildTree() {
        for (i in 0..3) {
            val treeNode = TreeNode("Parent  No.$i")
            if (i != 3) { // avoids creating child nodes for "parent" 3 (which then is not a parent, so the semantic in the displayed text becomes incorrect)
                for (j in 0..3) {
                    val treeNode1 = TreeNode("Child No.$j")
                    if (j != 2) { // avoids creating grand child nodes for child node 5
                        // For the child node without grand children there should not be any arrow displayed.
                        // In the demo code this can be handled in method 'SecondLevelNodeViewBinder.bindView' like this:
                        // imageView.setVisibility(treeNode.hasChild() ? View.VISIBLE : View.INVISIBLE);
                        for (k in 0..2) {
                            val treeNode2 = TreeNode("Grand Child No.$k")
                            treeNode1.addChild(treeNode2)
                        }
                    }
                    treeNode.addChild(treeNode1)
                }
            }
            root.addChild(treeNode)
        }
    }
}
