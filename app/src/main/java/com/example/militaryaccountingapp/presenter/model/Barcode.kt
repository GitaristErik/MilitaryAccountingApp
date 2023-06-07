package com.example.militaryaccountingapp.presenter.model

import java.io.Serializable

data class Barcode(
    val id: Int = 0,
    val code: String = "",
    val timestamp: Long = 0L
) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (other is Barcode) {
            other.id == id && other.code == code && other.timestamp == timestamp
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + code.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}