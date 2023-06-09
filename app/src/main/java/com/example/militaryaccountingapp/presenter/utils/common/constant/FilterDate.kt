package com.example.militaryaccountingapp.presenter.utils.common.constant

import com.google.android.material.datepicker.DateStringsCustom
import java.util.Date

sealed class FilterDate(
    open var date: Long = Date().time,
    open var displayName: String = DateStringsCustom.getYearMonthDay(date)
) {

    data class PickDay(
        override var date: Long = Date().time,
        override var displayName: String = DateStringsCustom.getYearMonthDay(date)
    ) : FilterDate()

    data class Before(
        override var date: Long = Date().time,
        override var displayName: String = DateStringsCustom.getYearMonthDay(date)
    ) : FilterDate()

    data class After(
        override var date: Long = Date().time,
        override var displayName: String = DateStringsCustom.getYearMonthDay(date)
    ) : FilterDate()

    data class Range(
        override var date: Long = Date().time,
        var startDate: Long = Date().time,
        override var displayName: String = DateStringsCustom.getDateRangeString(date, startDate)
    ) : FilterDate(date)

    override fun equals(other: Any?): Boolean {
        return if (other is FilterDate) {
            date == other.date && displayName == other.displayName
            if (this is Range && other is Range) {
                startDate == other.startDate
            } else {
                true
            }
        } else {
            false
        }
    }

    override fun hashCode(): Int = 31 * date.hashCode() + displayName.hashCode()
}