package com.example.militaryaccountingapp.domain.entity.data

data class Item(
    override var id: String = "",
    override var name: String = "",
    override var description: String = "",
    val count: Int = 0,
    override var imagesUrls: List<String> = emptyList(),
    override var barCodes: List<Barcode> = emptyList(),
    override var allParentIds: List<String> = emptyList(),
    override var parentId: String? = null,
    override var userId: String? = null,
) : Data()