package com.example.militaryaccountingapp.domain.entity.data

data class Item(
    override val id: Int,
    override val name: String,
    override val description: String,
    val count: Int,
    override val imageUrl: String?,
    val qrCode: String?,
    val barCode: String?,
) : Data()