package com.example.militaryaccountingapp.presenter.fragment.category

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentCategoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.category.CategoryViewModel.ViewData
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment :
    BaseViewModelFragment<FragmentCategoryBinding, ViewData, CategoryViewModel>() {

    override val viewModel: CategoryViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoryBinding
        get() = FragmentCategoryBinding::inflate

    override fun initializeView() {
        setupActionBar()
        setupQrCode()
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit -> {
                            // Handle edit text press
                            true
                        }

                        R.id.more -> {
                            // Handle more item (inside overflow menu) press
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }


    private fun setupQrCode() {
        val data = "This is the test data for qr code"
        val qrData = QrData.Text(data)

        val options = createQrVectorOptions {
            padding = .125f
            background {
                drawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_qr_code
                )
            }
            colors {
                dark = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.md_secondaryContainer)
                )
                ball = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.md_primaryContainer)
                )
                frame = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.md_primaryContainer)
                )
            }
            shapes {
                darkPixel = QrVectorPixelShape
                    .Circle(0.85f)
                ball = QrVectorBallShape
                    .Circle(.5f)
                frame = QrVectorFrameShape
                    .RoundCorners(.5f, .75f)
            }
        }
        val drawable: Drawable = QrCodeDrawable(qrData, options)
        binding.qrImage.setImageDrawable(drawable)
    }


    override fun render(data: ViewData) {
        log.d("render")
    }
}