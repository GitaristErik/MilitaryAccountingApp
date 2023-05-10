package com.example.militaryaccountingapp.presenter.utils.common.constant

import java.util.Date

sealed class FilterDate(
    open var date: Long = Date().time,
    open var displayName: String = ""
) {

    data class PickDay(
        override var date: Long = Date().time,
        override var displayName: String = ""
    ) : FilterDate()

    data class Before(
        override var date: Long = Date().time,
        override var displayName: String = ""
    ) : FilterDate()

    data class After(
        override var date: Long = Date().time,
        override var displayName: String = ""
    ) : FilterDate()

    data class Range(
        override var date: Long = Date().time,
        var endDate: Long = Date().time,
        override var displayName: String = ""
    ) : FilterDate(date)

    override fun equals(other: Any?): Boolean {
        return if (other is FilterDate) {
            date == other.date && displayName == other.displayName
            if (this is Range && other is Range) {
                endDate == other.endDate
            } else {
                true
            }
        } else {
            false
        }
    }

    override fun hashCode(): Int = 31 * date.hashCode() + displayName.hashCode()
}