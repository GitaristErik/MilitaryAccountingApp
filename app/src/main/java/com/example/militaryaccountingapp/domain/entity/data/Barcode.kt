package com.example.militaryaccountingapp.domain.entity.data

import java.io.Serializable

data class Barcode(
    var code: String = "",
    var timestamp: Long = 0L
) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (other is Barcode) {
            other.code == code && other.timestamp == timestamp
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}