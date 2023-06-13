package com.example.militaryaccountingapp.domain.entity.data

sealed class Data {
    abstract var id: String
    abstract var name: String
    abstract var description: String
    abstract var imagesUrls: List<String>
    open var parentId: String? = null
    open var allParentIds: List<String> = emptyList()
    open var userId: String? = null
    open var barCodes: List<Barcode> = emptyList()

    enum class Type {
        CATEGORY,
        ITEM
    }
}